package util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Classe utilitária para formatação de valores de energia e moeda.
 */
public class FormatadorEnergia {
    
    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    private static final NumberFormat numeroFormat = NumberFormat.getInstance(LOCALE_BR);
    private static final NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(LOCALE_BR);
    private static final NumberFormat energiaFormat = NumberFormat.getInstance(LOCALE_BR);
    
    static {
        numeroFormat.setMinimumFractionDigits(2);
        numeroFormat.setMaximumFractionDigits(2);
        
        energiaFormat.setMinimumFractionDigits(4);
        energiaFormat.setMaximumFractionDigits(4);
    }
    
    private FormatadorEnergia() {
        // Classe utilitária, não instanciável
    }
    
    /**
     * Formata valor de energia em kWh com 4 casas decimais.
     * @param kwh Valor em kWh
     * @return String formatada
     */
    public static String formatarEnergia(double kwh) {
        return energiaFormat.format(kwh);
    }
    
    /**
     * Formata valor de potência em Watts com 0 casas decimais.
     * @param watts Valor em Watts
     * @return String formatada
     */
    public static String formatarPotencia(double watts) {
        return String.format(LOCALE_BR, "%.0f", watts);
    }
    
    /**
     * Formata valor de potência em Watts com 2 casas decimais.
     * @param watts Valor em Watts
     * @return String formatada
     */
    public static String formatarPotenciaDetalhada(double watts) {
        return numeroFormat.format(watts);
    }
    
    /**
     * Formata valor de moeda (R$) com 2 casas decimais.
     * @param valor Valor em reais
     * @return String formatada com símbolo R$
     */
    public static String formatarMoeda(double valor) {
        return moedaFormat.format(valor);
    }
    
    /**
     * Formata tempo em segundos para formato legível (h:m:s ou apenas s).
     * @param segundos Quantidade de segundos
     * @return String formatada (ex: "2h 30m 15s" ou "30s")
     */
    public static String formatarTempo(long segundos) {
        if (segundos < 0) {
            return "0s";
        }
        
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        long secs = segundos % 60;
        
        if (horas > 0) {
            return String.format("%dh %dm %ds", horas, minutos, secs);
        } else if (minutos > 0) {
            return String.format("%dm %ds", minutos, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
    
    /**
     * Formata percentual com 1 casa decimal.
     * @param percentual Valor do percentual (0-100)
     * @return String formatada
     */
    public static String formatarPercentual(double percentual) {
        return String.format(LOCALE_BR, "%.1f%%", percentual);
    }
    
    /**
     * Formata número geral com 2 casas decimais.
     * @param numero Valor numérico
     * @return String formatada
     */
    public static String formatarNumero(double numero) {
        return numeroFormat.format(numero);
    }
}
