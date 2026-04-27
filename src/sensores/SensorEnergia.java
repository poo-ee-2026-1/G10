package sensores;

import dispositivos.DispositivoEletrico;

public class SensorEnergia {

    private static final double LIMITE_USO_WATTS = 1.0;

    private String idSensor;
    private DispositivoEletrico dispositivo;
    private double tensao;  
    private double corrente;
    private double duracaoLeituraSegundos;

    public SensorEnergia(String idSensor, DispositivoEletrico dispositivo, double tensao, double corrente) {
        this(idSensor, dispositivo, tensao, corrente, 1.0);
    }

    public SensorEnergia(
            String idSensor,
            DispositivoEletrico dispositivo,
            double tensao,
            double corrente,
            double duracaoLeituraSegundos) {
        this.idSensor = idSensor;
        this.dispositivo = dispositivo;
        this.tensao = tensao;
        this.corrente = corrente;
        this.duracaoLeituraSegundos = duracaoLeituraSegundos;
    }

    public double medirPotencia() {
        return tensao * corrente;
    }

    public boolean detectarUso() {
        return medirPotencia() > LIMITE_USO_WATTS;
    }

    public double medirEnergiaKWh() {
        return (medirPotencia() * duracaoLeituraSegundos) / 3600000.0;
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

    public double getDuracaoLeituraSegundos() {
        return duracaoLeituraSegundos;
    }
}
