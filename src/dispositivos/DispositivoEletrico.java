package dispositivos;

public class DispositivoEletrico {

    private String nome;
    private double potenciaWatts;
    private double horasUsoPorDia;

    public DispositivoEletrico(String nome, double potenciaWatts, double horasUsoPorDia) {
        this.nome = nome;
        this.potenciaWatts = potenciaWatts;
        this.horasUsoPorDia = horasUsoPorDia;
    }

    public double calcularConsumoDiario() {
        return (potenciaWatts * horasUsoPorDia) / 1000.0;
    }

    public double calcularConsumoMensal() {
        return calcularConsumoDiario() * 30;
    }

    public String getNome() {
        return nome;
    }

    public double getPotenciaWatts() {
        return potenciaWatts;
    }

    public double getHorasUsoPorDia() {
        return horasUsoPorDia;
    }
}