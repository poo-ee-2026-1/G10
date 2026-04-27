package simulacao;

import dispositivos.DispositivoEletrico;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class SimuladorEnergia {

    private final Random random;

    public SimuladorEnergia() {
        this.random = new Random();
    }

    public double simularPotenciaMedida(DispositivoEletrico dispositivo, Calendar dataSimulada) {
        double chanceLigado = obterChanceLigado(dispositivo, dataSimulada);
        if (random.nextDouble() > chanceLigado) {
            return 0;
        }

        double fatorCarga = obterFatorCarga(dispositivo, dataSimulada);
        double variacaoSensor = 0.94 + (random.nextDouble() * 0.12);
        return dispositivo.getPotenciaWatts() * fatorCarga * variacaoSensor;
    }

    private double obterChanceLigado(DispositivoEletrico dispositivo, Calendar dataSimulada) {
        String nome = dispositivo.getNome().toLowerCase(Locale.ROOT);
        int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);

        if (nome.contains("geladeira")) {
            return 0.75;
        }
        if (nome.contains("chuveiro")) {
            return (hora >= 6 && hora <= 8) || (hora >= 18 && hora <= 22) ? 0.38 : 0.03;
        }
        if (nome.contains("lampada") || nome.contains("luz")) {
            return hora >= 18 || hora <= 5 ? 0.72 : 0.08;
        }
        if (nome.contains("televisao") || nome.contains("tv")) {
            return hora >= 18 && hora <= 23 ? 0.58 : 0.12;
        }
        if (nome.contains("ar")) {
            return hora >= 12 && hora <= 23 ? 0.48 : 0.18;
        }
        return hora >= 7 && hora <= 23 ? 0.35 : 0.12;
    }

    private double obterFatorCarga(DispositivoEletrico dispositivo, Calendar dataSimulada) {
        String nome = dispositivo.getNome().toLowerCase(Locale.ROOT);
        int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);

        if (nome.contains("geladeira")) {
            return 0.35 + (random.nextDouble() * 0.35);
        }
        if (nome.contains("chuveiro")) {
            return 0.85 + (random.nextDouble() * 0.18);
        }
        if (nome.contains("lampada") || nome.contains("televisao") || nome.contains("tv")) {
            return 0.90 + (random.nextDouble() * 0.12);
        }
        if (nome.contains("ar")) {
            return hora >= 14 && hora <= 18 ? 0.80 + (random.nextDouble() * 0.25) : 0.45 + (random.nextDouble() * 0.25);
        }
        return 0.65 + (random.nextDouble() * 0.45);
    }
}
