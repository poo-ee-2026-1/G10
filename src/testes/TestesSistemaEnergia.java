package testes;

import dispositivos.DispositivoEletrico;
import util.ConstantesEnergia;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import sensores.SensorEnergia;
import simulacao.HistoricoConsumo;
import simulacao.LeituraSimulada;
import simulacao.RegistroConsumoDiario;
import simulacao.SimuladorEnergia;
import sistema.SistemaEnergetico;

/**
 * Testes unitários para o sistema de monitoramento de energia.
 */
public class TestesSistemaEnergia {

    private static final double TOLERANCIA = ConstantesEnergia.TOLERANCIA_CALCULO;

    public static void main(String[] args) {
        testarSensorCalculaPotenciaEEnergia();
        testarDispositivoProjetaPeloTempoMonitorado();
        testarHistoricoAvancaDataEmSequencia();
        testarHistoricoDivideLeituraNaViradaDoDia();
        testarSistemaProtegeListaDispositivos();
        testarSensorValidaEntradas();
        testarSimuladorAceitaEstrategiaCustomizada();

        System.out.println("Todos os testes passaram.");
    }

    private static void testarSensorCalculaPotenciaEEnergia() {
        DispositivoEletrico dispositivo = new DispositivoEletrico("Carga teste", 254);
        SensorEnergia sensor = new SensorEnergia("S-001", dispositivo, 127, 2, 3600);

        assertProximo("potencia medida", 254, sensor.medirPotencia());
        assertProximo("energia medida", 0.254, sensor.medirEnergiaKWh());
        assertVerdadeiro("sensor detecta uso", sensor.detectarUso());
    }

    private static void testarDispositivoProjetaPeloTempoMonitorado() {
        DispositivoEletrico dispositivo = new DispositivoEletrico("Aquecedor", 1000);
        dispositivo.registrarLeituraSensor(1000, 3600);

        assertProximo("energia coletada", 1, dispositivo.obterEnergiaColetadaKWh());
        assertProximo("projecao diaria", 24, dispositivo.projetarConsumoPorDiasKWh(1));
        assertProximo("projecao mensal", 720, dispositivo.projetarConsumoMensalKWh());
        assertProximo("potencia media", 1000, dispositivo.obterPotenciaMediaColetadaWatts());
    }

    private static void testarHistoricoAvancaDataEmSequencia() {
        HistoricoConsumo historico = new HistoricoConsumo();
        Calendar inicio = historico.obterDataAtual();
        List<LeituraSimulada> leituras = new ArrayList<LeituraSimulada>();
        leituras.add(new LeituraSimulada("Carga teste", 1000));

        historico.registrarPeriodo(leituras, 86400);

        Calendar fimEsperado = (Calendar) inicio.clone();
        fimEsperado.add(Calendar.DAY_OF_MONTH, 1);

        assertIgual("ano avanca em sequencia", fimEsperado.get(Calendar.YEAR),
                historico.obterDataAtual().get(Calendar.YEAR));
        assertIgual("dia avanca em sequencia", fimEsperado.get(Calendar.DAY_OF_YEAR),
                historico.obterDataAtual().get(Calendar.DAY_OF_YEAR));
        assertProximo("energia registrada apos um dia", 24, calcularEnergiaEntreAnos(historico, inicio));
    }

    private static void testarHistoricoDivideLeituraNaViradaDoDia() {
        HistoricoConsumo historico = new HistoricoConsumo();
        Calendar inicio = historico.obterDataAtual();
        List<LeituraSimulada> leituras = new ArrayList<LeituraSimulada>();
        leituras.add(new LeituraSimulada("Carga teste", 3600));

        historico.registrarPeriodo(leituras, 172800);

        RegistroConsumoDiario primeiroDia = historico.obterRegistroDia(
                inicio.get(Calendar.YEAR),
                inicio.get(Calendar.MONTH),
                inicio.get(Calendar.DAY_OF_MONTH));

        Calendar segundoDiaData = (Calendar) inicio.clone();
        segundoDiaData.add(Calendar.DAY_OF_MONTH, 1);
        RegistroConsumoDiario segundoDia = historico.obterRegistroDia(
                segundoDiaData.get(Calendar.YEAR),
                segundoDiaData.get(Calendar.MONTH),
                segundoDiaData.get(Calendar.DAY_OF_MONTH));

        assertVerdadeiro("primeiro dia tem registro", primeiroDia != null);
        assertVerdadeiro("segundo dia tem registro", segundoDia != null);
        assertVerdadeiro("primeiro dia recebeu energia", primeiroDia.obterEnergiaKWh() > 0);
        assertVerdadeiro("segundo dia recebeu energia", segundoDia.obterEnergiaKWh() > 0);
    }

    private static void testarSistemaProtegeListaDispositivos() {
        SistemaEnergetico sistema = new SistemaEnergetico(0.75);
        sistema.adicionarDispositivo(new DispositivoEletrico("Lampada", 60));

        try {
            sistema.obterDispositivos().clear();
            throw new AssertionError("lista de dispositivos deveria ser imutavel");
        } catch (UnsupportedOperationException esperado) {
            assertIgual("dispositivo preservado", 1, sistema.obterQuantidadeDispositivos());
        }
    }

    private static void testarSensorValidaEntradas() {
        DispositivoEletrico dispositivo = new DispositivoEletrico("Carga teste", 100);

        assertLanca("sensor rejeita id vazio", new Runnable() {
            @Override
            public void run() {
                new SensorEnergia("", dispositivo, 127, 1);
            }
        });
        assertLanca("sensor rejeita corrente negativa", new Runnable() {
            @Override
            public void run() {
                new SensorEnergia("S-002", dispositivo, 127, -1);
            }
        });
    }

    private static void testarSimuladorAceitaEstrategiaCustomizada() {
        SimuladorEnergia simulador = new SimuladorEnergia();
        simulador.registrarEstrategia(new simulacao.EstrategiaSimulacaoDispositivo() {
            @Override
            public boolean atende(DispositivoEletrico dispositivo) {
                return dispositivo.getNome().contains("Sempre Ligado");
            }

            @Override
            public double obterChanceLigado(Calendar dataSimulada) {
                return 1.0;
            }

            @Override
            public double obterFatorCarga(Calendar dataSimulada, java.util.Random random) {
                return 1.0;
            }
        });

        DispositivoEletrico dispositivo = new DispositivoEletrico("Sempre Ligado", 100);
        double potencia = simulador.simularPotenciaMedida(dispositivo, Calendar.getInstance());

        assertVerdadeiro("estrategia customizada gera potencia", potencia > 0);
    }

    private static void assertProximo(String nome, double esperado, double obtido) {
        if (Math.abs(esperado - obtido) > TOLERANCIA) {
            throw new AssertionError(nome + " esperado " + esperado + ", obtido " + obtido);
        }
    }

    private static double calcularEnergiaEntreAnos(HistoricoConsumo historico, Calendar inicio) {
        int anoInicial = inicio.get(Calendar.YEAR);
        int anoAtual = historico.obterDataAtual().get(Calendar.YEAR);
        double total = 0;

        for (int ano = anoInicial; ano <= anoAtual; ano++) {
            total += historico.calcularEnergiaAno(ano);
        }

        return total;
    }

    private static void assertIgual(String nome, int esperado, int obtido) {
        if (esperado != obtido) {
            throw new AssertionError(nome + " esperado " + esperado + ", obtido " + obtido);
        }
    }

    private static void assertVerdadeiro(String nome, boolean condicao) {
        if (!condicao) {
            throw new AssertionError(nome);
        }
    }

    private static void assertLanca(String nome, Runnable acao) {
        try {
            acao.run();
            throw new AssertionError(nome);
        } catch (IllegalArgumentException esperado) {
            // esperado
        }
    }
}
