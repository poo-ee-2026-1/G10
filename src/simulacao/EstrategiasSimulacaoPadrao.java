package simulacao;

import dispositivos.DispositivoEletrico;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Estrategias padrao usadas pelo simulador de energia.
 */
final class EstrategiasSimulacaoPadrao {

    private EstrategiasSimulacaoPadrao() {
    }

    static abstract class PorNome implements EstrategiaSimulacaoDispositivo {
        private final String[] termos;

        PorNome(String... termos) {
            this.termos = termos;
        }

        @Override
        public boolean atende(DispositivoEletrico dispositivo) {
            String nome = dispositivo.getNome().toLowerCase(Locale.ROOT);
            for (String termo : termos) {
                if (nome.contains(termo)) {
                    return true;
                }
            }
            return false;
        }
    }

    static final class Geladeira extends PorNome {
        Geladeira() {
            super("geladeira");
        }

        @Override
        public double obterChanceLigado(Calendar dataSimulada) {
            return 0.75;
        }

        @Override
        public double obterFatorCarga(Calendar dataSimulada, Random random) {
            return 0.35 + (random.nextDouble() * 0.35);
        }
    }

    static final class Chuveiro extends PorNome {
        Chuveiro() {
            super("chuveiro");
        }

        @Override
        public double obterChanceLigado(Calendar dataSimulada) {
            int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);
            return (hora >= 6 && hora <= 8) || (hora >= 18 && hora <= 22) ? 0.38 : 0.03;
        }

        @Override
        public double obterFatorCarga(Calendar dataSimulada, Random random) {
            return 0.85 + (random.nextDouble() * 0.18);
        }
    }

    static final class Iluminacao extends PorNome {
        Iluminacao() {
            super("lampada", "luz");
        }

        @Override
        public double obterChanceLigado(Calendar dataSimulada) {
            int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);
            return hora >= 18 || hora <= 5 ? 0.72 : 0.08;
        }

        @Override
        public double obterFatorCarga(Calendar dataSimulada, Random random) {
            return 0.90 + (random.nextDouble() * 0.12);
        }
    }

    static final class Televisao extends PorNome {
        Televisao() {
            super("televisao", "tv");
        }

        @Override
        public double obterChanceLigado(Calendar dataSimulada) {
            int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);
            return hora >= 18 && hora <= 23 ? 0.58 : 0.12;
        }

        @Override
        public double obterFatorCarga(Calendar dataSimulada, Random random) {
            return 0.90 + (random.nextDouble() * 0.12);
        }
    }

    static final class ArCondicionado extends PorNome {
        ArCondicionado() {
            super("ar");
        }

        @Override
        public double obterChanceLigado(Calendar dataSimulada) {
            int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);
            return hora >= 12 && hora <= 23 ? 0.48 : 0.18;
        }

        @Override
        public double obterFatorCarga(Calendar dataSimulada, Random random) {
            int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);
            return hora >= 14 && hora <= 18
                    ? 0.80 + (random.nextDouble() * 0.25)
                    : 0.45 + (random.nextDouble() * 0.25);
        }
    }

    static final class Generica implements EstrategiaSimulacaoDispositivo {
        @Override
        public boolean atende(DispositivoEletrico dispositivo) {
            return true;
        }

        @Override
        public double obterChanceLigado(Calendar dataSimulada) {
            int hora = dataSimulada.get(Calendar.HOUR_OF_DAY);
            return hora >= 7 && hora <= 23 ? 0.35 : 0.12;
        }

        @Override
        public double obterFatorCarga(Calendar dataSimulada, Random random) {
            return 0.65 + (random.nextDouble() * 0.45);
        }
    }
}
