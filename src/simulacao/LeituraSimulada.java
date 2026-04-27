package simulacao;

public class LeituraSimulada {

    private final String nomeDispositivo;
    private final double potenciaWatts;

    public LeituraSimulada(String nomeDispositivo, double potenciaWatts) {
        this.nomeDispositivo = nomeDispositivo;
        this.potenciaWatts = potenciaWatts;
    }

    public String obterNomeDispositivo() {
        return nomeDispositivo;
    }

    public double obterPotenciaWatts() {
        return potenciaWatts;
    }
}
