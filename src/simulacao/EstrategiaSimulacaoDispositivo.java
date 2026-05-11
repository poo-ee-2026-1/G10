package simulacao;

import dispositivos.DispositivoEletrico;
import java.util.Calendar;
import java.util.Random;

/**
 * Define o comportamento de simulacao de um tipo de dispositivo.
 */
public interface EstrategiaSimulacaoDispositivo {

    /**
     * Indica se a estrategia atende ao dispositivo informado.
     */
    boolean atende(DispositivoEletrico dispositivo);

    /**
     * Retorna a chance do dispositivo estar ligado no horario simulado.
     */
    double obterChanceLigado(Calendar dataSimulada);

    /**
     * Retorna o fator de carga aplicado sobre a potencia nominal.
     */
    double obterFatorCarga(Calendar dataSimulada, Random random);
}
