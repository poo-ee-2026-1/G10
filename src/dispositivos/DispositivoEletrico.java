package dispositivos;

public class DispositivoEletrico {

    private static final double LIMITE_USO_WATTS = 1.0;
    private String nome;
    private double potenciaWatts;
    private long tempoUsoSegundos;
    private long tempoMonitoradoSegundos;
    private double energiaColetadaKWh;

    public DispositivoEletrico(String nome, double potenciaWatts) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do dispositivo nao pode ser vazio.");
        }
        if (potenciaWatts <= 0) {
            throw new IllegalArgumentException("Potencia deve ser maior que zero.");
        }

        this.nome = nome;
        this.potenciaWatts = potenciaWatts;
        this.tempoUsoSegundos = 0;
        this.tempoMonitoradoSegundos = 0;
        this.energiaColetadaKWh = 0;
    }

    public DispositivoEletrico(String nome, double potenciaWatts, double horasUsoPorDia) {
        this(nome, potenciaWatts);
        registrarLeituraSensor(potenciaWatts, horasUsoPorDia * 3600);
    }

    public void registrarLeituraSensor(double potenciaMedidaWatts, double segundosMonitorados) {
        if (segundosMonitorados <= 0) {
            return;
        }

        long segundos = Math.max(1, Math.round(segundosMonitorados));
        tempoMonitoradoSegundos += segundos;

        if (potenciaMedidaWatts > LIMITE_USO_WATTS) {
            tempoUsoSegundos += segundos;
            energiaColetadaKWh += (potenciaMedidaWatts * segundosMonitorados) / 3600000.0;
        }
    }

    public void registrarUsoPorSensor(double potenciaMedidaWatts, double segundosMonitorados) {
        registrarLeituraSensor(potenciaMedidaWatts, segundosMonitorados);
    }

    public double calcularConsumoDiario() {
        return obterEnergiaColetadaKWh();
    }

    public double calcularConsumoMensal() {
        return projetarConsumoMensalKWh();
    }

    public double calcularProjecaoMensal() {
        return projetarConsumoMensalKWh();
    }

    public double calcularProjecaoPorDias(int quantidadeDias) {
        return projetarConsumoPorDiasKWh(quantidadeDias);
    }

    public double projetarConsumoMensalKWh() {
        return projetarConsumoPorDiasKWh(30);
    }

    public double projetarConsumoPorDiasKWh(int quantidadeDias) {
        if (tempoMonitoradoSegundos == 0) {
            return 0;
        }

        double consumoPorSegundo = energiaColetadaKWh / tempoMonitoradoSegundos;
        return consumoPorSegundo * quantidadeDias * 24 * 60 * 60;
    }

    public void zerarColetaSensor() {
        tempoUsoSegundos = 0;
        tempoMonitoradoSegundos = 0;
        energiaColetadaKWh = 0;
    }

    public String getNome() {
        return nome;
    }

    public double getPotenciaWatts() {
        return potenciaWatts;
    }

    public double getHorasUsoPorDia() {
        return obterTempoUsoHoras();
    }

    public long obterTempoUsoSegundos() {
        return tempoUsoSegundos;
    }

    public long obterTempoMonitoradoSegundos() {
        return tempoMonitoradoSegundos;
    }

    public double obterTempoUsoHoras() {
        return tempoUsoSegundos / 3600.0;
    }

    public double obterEnergiaColetadaKWh() {
        return energiaColetadaKWh;
    }

    public double obterPotenciaMediaColetadaWatts() {
        if (tempoMonitoradoSegundos == 0) {
            return 0;
        }

        return (energiaColetadaKWh * 3600000.0) / tempoMonitoradoSegundos;
    }
}
