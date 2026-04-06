package sensores;

import dispositivos.DispositivoEletrico;

public class SensorEnergia {

    private String idSensor;
    private DispositivoEletrico dispositivo;
    private double tensao;  
    private double corrente;

    public SensorEnergia(String idSensor, DispositivoEletrico dispositivo, double tensao, double corrente) {
        this.idSensor = idSensor;
        this.dispositivo = dispositivo;
        this.tensao = tensao;
        this.corrente = corrente;
    }

    public double medirPotencia() {
        return tensao * corrente;
    }

    public void registrarLeitura() {
        double potencia = medirPotencia();

        System.out.println("Sensor: " + idSensor);
        System.out.println("Dispositivo: " + dispositivo.getNome());
        System.out.println("Tensão: " + tensao + " V");
        System.out.println("Corrente: " + corrente + " A");
        System.out.println("Potência medida: " + potencia + " W");
        System.out.println("-----------------------------");
    }

    public String getIdSensor() {
        return idSensor;
    }

    public DispositivoEletrico getDispositivo() {
        return dispositivo;
    }

    public double getTensao() {
        return tensao;
    }

    public double getCorrente() {
        return corrente;
    }
}