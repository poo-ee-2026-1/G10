package simulacao;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroConsumoDiario {

    private final Calendar data;
    private long tempoMonitoradoSegundos;
    private double energiaKWh;
    private final Map<String, Double> energiaPorDispositivo;

    public RegistroConsumoDiario(Calendar data) {
        this.data = (Calendar) data.clone();
        this.tempoMonitoradoSegundos = 0;
        this.energiaKWh = 0;
        this.energiaPorDispositivo = new LinkedHashMap<String, Double>();
    }

    public void registrarTempoMonitorado(long segundosMonitorados) {
        tempoMonitoradoSegundos += segundosMonitorados;
    }

    public void registrarEnergia(String nomeDispositivo, double energiaKWh) {
        if (energiaKWh <= 0) {
            return;
        }

        this.energiaKWh += energiaKWh;

        Double energiaAtual = energiaPorDispositivo.get(nomeDispositivo);
        if (energiaAtual == null) {
            energiaAtual = Double.valueOf(0);
        }

        energiaPorDispositivo.put(nomeDispositivo, Double.valueOf(energiaAtual.doubleValue() + energiaKWh));
    }

    public Calendar obterData() {
        return (Calendar) data.clone();
    }

    public long obterTempoMonitoradoSegundos() {
        return tempoMonitoradoSegundos;
    }

    public double obterEnergiaKWh() {
        return energiaKWh;
    }

    public Map<String, Double> obterEnergiaPorDispositivo() {
        return energiaPorDispositivo;
    }

    public String obterMaiorConsumidor() {
        String maiorConsumidor = "-";
        double maiorEnergia = 0;

        for (Map.Entry<String, Double> entrada : energiaPorDispositivo.entrySet()) {
            if (entrada.getValue().doubleValue() > maiorEnergia) {
                maiorEnergia = entrada.getValue().doubleValue();
                maiorConsumidor = entrada.getKey();
            }
        }

        return maiorConsumidor;
    }
}
