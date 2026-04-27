package testes;

import dispositivos.DispositivoEletrico;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import sensores.SensorEnergia;
import simulacao.HistoricoConsumo;
import simulacao.LeituraSimulada;
import simulacao.RegistroConsumoDiario;

public class TestesSistemaEnergia {

    private static final double TOLERANCIA = 0.0001;

    public static void main(String[] args) {
        testarSensorCalculaPotenciaEEnergia();
        testarDispositivoProjetaPeloTempoMonitorado();
        testarHistoricoAvancaDataEmSequencia();
        testarHistoricoDivideLeituraNaViradaDoDia();

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
}
