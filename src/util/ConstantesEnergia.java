package util;

/**
 * Classe com constantes globais do sistema de monitoramento de energia.
 */
public class ConstantesEnergia {
    
    // Constantes elétricas
    public static final double TENSAO_PADRAO_VOLT = 127.0;
    public static final double LIMITE_USO_WATTS = 1.0;
    public static final double LIMITE_ALERTA_POTENCIA_WATTS = 6000.0;
    
    // Conversões de tempo
    public static final int SEGUNDOS_POR_HORA = 3600;
    public static final int MINUTOS_POR_HORA = 60;
    public static final int HORAS_POR_DIA = 24;
    public static final int DIAS_POR_MES = 30;
    public static final int MESES_POR_ANO = 12;
    
    // Conversão de energia
    public static final int MILIWATTS_POR_WATT = 1000;
    public static final double FATOR_CONVERSAO_KWATTS = 1000.0; // W para kW
    
    // GUI e interface
    public static final int INTERVALO_ATUALIZACAO_MS = 1000;
    public static final int LARGURA_JANELA_PADRAO = 1200;
    public static final int ALTURA_JANELA_PADRAO = 700;
    public static final int ESPACAMENTO_COMPONENTES = 10;
    
    // Fatores de simulação
    public static final double FATOR_CARGA_MINIMO = 0.1;
    public static final double FATOR_CARGA_MAXIMO = 1.0;
    public static final double VARIACAO_SENSOR_MINIMA = 0.94;
    public static final double VARIACAO_SENSOR_MAXIMA = 1.06;
    
    // Precisão de cálculos
    public static final double TOLERANCIA_CALCULO = 0.0001;
    
    private ConstantesEnergia() {
        // Classe utilitária, não instanciável
    }
}
