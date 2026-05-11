package simulacao;

import dispositivos.DispositivoEletrico;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import util.ConstantesEnergia;

/**
 * Simula medicoes de potencia para dispositivos com estrategias por tipo de aparelho.
 */
public class SimuladorEnergia {

    private final Random random;
    private final List<EstrategiaSimulacaoDispositivo> estrategias;
    private final EstrategiaSimulacaoDispositivo estrategiaGenerica;

    public SimuladorEnergia() {
        this.random = new Random();
        this.estrategias = new ArrayList<EstrategiaSimulacaoDispositivo>();
        this.estrategiaGenerica = new EstrategiasSimulacaoPadrao.Generica();

        registrarEstrategiaPadrao(new EstrategiasSimulacaoPadrao.Geladeira());
        registrarEstrategiaPadrao(new EstrategiasSimulacaoPadrao.Chuveiro());
        registrarEstrategiaPadrao(new EstrategiasSimulacaoPadrao.Iluminacao());
        registrarEstrategiaPadrao(new EstrategiasSimulacaoPadrao.Televisao());
        registrarEstrategiaPadrao(new EstrategiasSimulacaoPadrao.ArCondicionado());
    }

    /**
     * Permite adicionar novos comportamentos sem alterar o simulador.
     */
    public void registrarEstrategia(EstrategiaSimulacaoDispositivo estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("A estrategia de simulacao nao pode ser nula.");
        }
        this.estrategias.add(0, estrategia);
    }

    private void registrarEstrategiaPadrao(EstrategiaSimulacaoDispositivo estrategia) {
        this.estrategias.add(estrategia);
    }

    /**
     * Simula uma leitura de potencia para um dispositivo em um horario especifico.
     *
     * @param dispositivo Dispositivo a simular
     * @param dataSimulada Data e hora da simulacao
     * @return Potencia simulada em Watts
     */
    public double simularPotenciaMedida(DispositivoEletrico dispositivo, Calendar dataSimulada) {
        EstrategiaSimulacaoDispositivo estrategia = obterEstrategia(dispositivo);
        double chanceLigado = estrategia.obterChanceLigado(dataSimulada);
        if (random.nextDouble() > chanceLigado) {
            return 0;
        }

        double fatorCarga = estrategia.obterFatorCarga(dataSimulada, random);
        double variacaoSensor = ConstantesEnergia.VARIACAO_SENSOR_MINIMA
                + (random.nextDouble()
                * (ConstantesEnergia.VARIACAO_SENSOR_MAXIMA - ConstantesEnergia.VARIACAO_SENSOR_MINIMA));
        return dispositivo.getPotenciaWatts() * fatorCarga * variacaoSensor;
    }

    private EstrategiaSimulacaoDispositivo obterEstrategia(DispositivoEletrico dispositivo) {
        for (EstrategiaSimulacaoDispositivo estrategia : estrategias) {
            if (estrategia.atende(dispositivo)) {
                return estrategia;
            }
        }
        return estrategiaGenerica;
    }
}
