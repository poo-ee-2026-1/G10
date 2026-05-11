package sensores;

import dispositivos.DispositivoEletrico;
import util.ConstantesEnergia;
import util.FormatadorEnergia;

/**
 * Sensor de energia que mede potencia, corrente e tensao de um dispositivo.
 */
public class SensorEnergia {

    private final String idSensor;
    private final DispositivoEletrico dispositivo;
    private final double tensao;
    private final double corrente;
    private final double duracaoLeituraSegundos;

    /**
     * Cria um sensor com duracao de leitura padrao de 1 segundo.
     */
    public SensorEnergia(String idSensor, DispositivoEletrico dispositivo, double tensao, double corrente) {
        this(idSensor, dispositivo, tensao, corrente, 1.0);
    }

    /**
     * Cria um sensor com duracao de leitura customizavel.
     */
    public SensorEnergia(
            String idSensor,
            DispositivoEletrico dispositivo,
            double tensao,
            double corrente,
            double duracaoLeituraSegundos) {
        if (idSensor == null || idSensor.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do sensor nao pode ser vazio.");
        }
        if (dispositivo == null) {
            throw new IllegalArgumentException("O dispositivo do sensor nao pode ser nulo.");
        }
        if (tensao <= 0) {
            throw new IllegalArgumentException("A tensao deve ser maior que zero.");
        }
        if (corrente < 0) {
            throw new IllegalArgumentException("A corrente nao pode ser negativa.");
        }
        if (duracaoLeituraSegundos <= 0) {
            throw new IllegalArgumentException("A duracao da leitura deve ser maior que zero.");
        }

        this.idSensor = idSensor.trim();
        this.dispositivo = dispositivo;
        this.tensao = tensao;
        this.corrente = corrente;
        this.duracaoLeituraSegundos = duracaoLeituraSegundos;
    }

    /**
     * Calcula a potencia instantanea em Watts (V x A).
     */
    public double medirPotencia() {
        return tensao * corrente;
    }

    /**
     * Detecta se o dispositivo esta em uso (potencia > limite).
     */
    public boolean detectarUso() {
        return medirPotencia() > ConstantesEnergia.LIMITE_USO_WATTS;
    }

    /**
     * Calcula a energia consumida em kWh durante a duracao da leitura.
     */
    public double medirEnergiaKWh() {
        return (medirPotencia() * duracaoLeituraSegundos)
                / (ConstantesEnergia.SEGUNDOS_POR_HORA * ConstantesEnergia.FATOR_CONVERSAO_KWATTS);
    }

    /**
     * Exibe informacoes da leitura do sensor no console.
     */
    public void registrarLeitura() {
        System.out.println(gerarResumoLeitura());
    }

    /**
     * Gera informacoes da leitura do sensor sem depender diretamente do console.
     */
    public String gerarResumoLeitura() {
        StringBuilder resumo = new StringBuilder();
        resumo.append("Sensor: ").append(idSensor).append(System.lineSeparator());
        resumo.append("Dispositivo: ").append(dispositivo.getNome()).append(System.lineSeparator());
        resumo.append("Tensao: ").append(FormatadorEnergia.formatarNumero(tensao)).append(" V").append(System.lineSeparator());
        resumo.append("Corrente: ").append(FormatadorEnergia.formatarNumero(corrente)).append(" A").append(System.lineSeparator());
        resumo.append("Potencia medida: ")
                .append(FormatadorEnergia.formatarPotenciaDetalhada(medirPotencia()))
                .append(" W")
                .append(System.lineSeparator());
        resumo.append("-----------------------------");
        return resumo.toString();
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
