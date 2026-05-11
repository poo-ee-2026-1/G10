package dispositivos;

import util.ConstantesEnergia;

/**
 * Representa um dispositivo elétrico com capacidade de monitoramento de consumo de energia.
 * Registra leituras de sensores e calcula consumo em tempo real.
 */
public class DispositivoEletrico {

    private String nome;
    private double potenciaWatts;
    private long tempoUsoSegundos;
    private long tempoMonitoradoSegundos;
    private double energiaColetadaKWh;

    /**
     * Cria um novo dispositivo elétrico.
     * 
     * @param nome Nome do dispositivo (não pode ser vazio)
     * @param potenciaWatts Potência nominal em Watts (deve ser > 0)
     * @throws IllegalArgumentException se nome for vazio ou potência for inválida
     */
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

    /**
     * Cria um novo dispositivo com consumo estimado inicial.
     * 
     * @param nome Nome do dispositivo
     * @param potenciaWatts Potência nominal em Watts
     * @param horasUsoPorDia Horas de uso esperadas por dia (para projeção inicial)
     */
    public DispositivoEletrico(String nome, double potenciaWatts, double horasUsoPorDia) {
        this(nome, potenciaWatts);
        registrarLeituraSensor(potenciaWatts, horasUsoPorDia * ConstantesEnergia.SEGUNDOS_POR_HORA);
    }

    /**
     * Registra uma leitura do sensor de energia para este dispositivo.
     * 
     * @param potenciaMedidaWatts Potência medida em Watts
     * @param segundosMonitorados Duração do monitoramento em segundos
     */
    public void registrarLeituraSensor(double potenciaMedidaWatts, double segundosMonitorados) {
        if (segundosMonitorados <= 0) {
            return;
        }

        long segundos = Math.max(1, Math.round(segundosMonitorados));
        tempoMonitoradoSegundos += segundos;

        if (potenciaMedidaWatts > ConstantesEnergia.LIMITE_USO_WATTS) {
            tempoUsoSegundos += segundos;
            energiaColetadaKWh += (potenciaMedidaWatts * segundosMonitorados) / (ConstantesEnergia.SEGUNDOS_POR_HORA * 1000.0);
        }
    }

    /**
     * Retorna a energia coletada até agora em kWh.
     * 
     * @return Consumo em kWh
     */
    public double obterEnergiaColetadaKWh() {
        return energiaColetadaKWh;
    }

    /**
     * Projeta o consumo para uma quantidade específica de dias.
     * 
     * @param quantidadeDias Número de dias para projetar
     * @return Consumo projetado em kWh
     */
    public double projetarConsumoPorDiasKWh(int quantidadeDias) {
        if (tempoMonitoradoSegundos == 0) {
            return 0;
        }

        double consumoPorSegundo = energiaColetadaKWh / tempoMonitoradoSegundos;
        return consumoPorSegundo * quantidadeDias * ConstantesEnergia.HORAS_POR_DIA * ConstantesEnergia.MINUTOS_POR_HORA * ConstantesEnergia.MINUTOS_POR_HORA;
    }

    /**
     * Projeta o consumo para um mês (30 dias).
     * 
     * @return Consumo projetado em kWh
     */
    public double projetarConsumoMensalKWh() {
        return projetarConsumoPorDiasKWh(ConstantesEnergia.DIAS_POR_MES);
    }

    /**
     * Limpa todos os dados coletados pelo sensor (zera contadores).
     */
    public void zerarColetaSensor() {
        tempoUsoSegundos = 0;
        tempoMonitoradoSegundos = 0;
        energiaColetadaKWh = 0;
    }

    /**
     * @return Nome do dispositivo
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return Potência nominal em Watts
     */
    public double getPotenciaWatts() {
        return potenciaWatts;
    }

    /**
     * @return Tempo de uso em segundos
     */
    public long obterTempoUsoSegundos() {
        return tempoUsoSegundos;
    }

    /**
     * @return Tempo monitorado em segundos
     */
    public long obterTempoMonitoradoSegundos() {
        return tempoMonitoradoSegundos;
    }

    /**
     * @return Tempo de uso em horas
     */
    public double obterTempoUsoHoras() {
        return tempoUsoSegundos / (double) ConstantesEnergia.SEGUNDOS_POR_HORA;
    }

    /**
     * Calcula a potência média consumida baseado na energia coletada.
     * 
     * @return Potência média em Watts
     */
    public double obterPotenciaMediaColetadaWatts() {
        if (tempoMonitoradoSegundos == 0) {
            return 0;
        }

        return (energiaColetadaKWh * ConstantesEnergia.SEGUNDOS_POR_HORA * 1000.0) / tempoMonitoradoSegundos;
    }
}
