package sistema;

import dispositivos.DispositivoEletrico;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import util.FormatadorEnergia;

/**
 * Sistema principal de gerenciamento de dispositivos eletricos e consumo de energia.
 * Coordena dispositivos, calcula consumo e custos.
 */
public class SistemaEnergetico {

    private final List<DispositivoEletrico> listaDispositivos;
    private double tarifaEnergia; // em R$ por kWh

    /**
     * Cria um novo sistema energetico com tarifa especificada.
     *
     * @param tarifaEnergia Tarifa de energia em R$ por kWh
     */
    public SistemaEnergetico(double tarifaEnergia) {
        this.listaDispositivos = new ArrayList<DispositivoEletrico>();
        definirTarifaEnergia(tarifaEnergia);
    }

    /**
     * Adiciona um dispositivo ao sistema.
     *
     * @param dispositivo Dispositivo a ser adicionado
     */
    public void adicionarDispositivo(DispositivoEletrico dispositivo) {
        if (dispositivo == null) {
            throw new IllegalArgumentException("O dispositivo nao pode ser nulo.");
        }
        listaDispositivos.add(dispositivo);
    }

    /**
     * Remove um dispositivo do sistema.
     *
     * @param dispositivo Dispositivo a ser removido
     */
    public void removerDispositivo(DispositivoEletrico dispositivo) {
        if (dispositivo == null) {
            throw new IllegalArgumentException("O dispositivo nao pode ser nulo.");
        }
        listaDispositivos.remove(dispositivo);
    }

    /**
     * Calcula o consumo total diario (energia coletada) em kWh.
     *
     * @return Consumo em kWh
     */
    public double calcularConsumoDiario() {
        double consumoTotal = 0;
        for (DispositivoEletrico dispositivo : listaDispositivos) {
            consumoTotal += dispositivo.obterEnergiaColetadaKWh();
        }
        return consumoTotal;
    }

    /**
     * Calcula o consumo total mensal projetado em kWh.
     *
     * @return Consumo projetado em kWh
     */
    public double calcularConsumoMensal() {
        double consumoTotal = 0;
        for (DispositivoEletrico dispositivo : listaDispositivos) {
            consumoTotal += dispositivo.projetarConsumoMensalKWh();
        }
        return consumoTotal;
    }

    /**
     * Calcula o custo diario baseado no consumo coletado.
     *
     * @return Custo em reais
     */
    public double calcularCustoDiario() {
        return calcularConsumoDiario() * tarifaEnergia;
    }

    /**
     * Calcula o custo mensal baseado na projecao.
     *
     * @return Custo projetado em reais
     */
    public double calcularCustoMensal() {
        return calcularConsumoMensal() * tarifaEnergia;
    }

    /**
     * Exibe um resumo formatado do sistema com todos os dispositivos e totais.
     */
    public void exibirResumo() {
        System.out.print(gerarResumo());
    }

    /**
     * Gera um resumo formatado do sistema.
     *
     * @return Texto do resumo
     */
    public String gerarResumo() {
        StringBuilder resumo = new StringBuilder();
        resumo.append(System.lineSeparator())
                .append("========== RESUMO DO SISTEMA ENERGETICO ==========")
                .append(System.lineSeparator());
        resumo.append("Tarifa de energia: ")
                .append(FormatadorEnergia.formatarMoeda(tarifaEnergia))
                .append(" por kWh")
                .append(System.lineSeparator());
        resumo.append(System.lineSeparator()).append("--- Dispositivos ---").append(System.lineSeparator());

        for (DispositivoEletrico dispositivo : listaDispositivos) {
            resumo.append(System.lineSeparator()).append("- ").append(dispositivo.getNome()).append(System.lineSeparator());
            resumo.append("  Potencia: ")
                    .append(FormatadorEnergia.formatarPotencia(dispositivo.getPotenciaWatts()))
                    .append(" W")
                    .append(System.lineSeparator());
            resumo.append("  Tempo coletado: ")
                    .append(FormatadorEnergia.formatarTempo(dispositivo.obterTempoUsoSegundos()))
                    .append(System.lineSeparator());
            resumo.append("  Energia coletada: ")
                    .append(FormatadorEnergia.formatarEnergia(dispositivo.obterEnergiaColetadaKWh()))
                    .append(" kWh")
                    .append(System.lineSeparator());
            resumo.append("  Projecao mensal: ")
                    .append(FormatadorEnergia.formatarEnergia(dispositivo.projetarConsumoMensalKWh()))
                    .append(" kWh")
                    .append(System.lineSeparator());
        }

        resumo.append(System.lineSeparator()).append("--- Totais ---").append(System.lineSeparator());
        resumo.append("Energia coletada total: ")
                .append(FormatadorEnergia.formatarEnergia(calcularConsumoDiario()))
                .append(" kWh")
                .append(System.lineSeparator());
        resumo.append("Projecao mensal total: ")
                .append(FormatadorEnergia.formatarEnergia(calcularConsumoMensal()))
                .append(" kWh")
                .append(System.lineSeparator());
        resumo.append("Custo coletado: ")
                .append(FormatadorEnergia.formatarMoeda(calcularCustoDiario()))
                .append(System.lineSeparator());
        resumo.append("Custo mensal projetado: ")
                .append(FormatadorEnergia.formatarMoeda(calcularCustoMensal()))
                .append(System.lineSeparator());
        resumo.append("===================================================")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        return resumo.toString();
    }

    /**
     * @return Lista de dispositivos do sistema
     */
    public List<DispositivoEletrico> obterDispositivos() {
        return Collections.unmodifiableList(listaDispositivos);
    }

    /**
     * @return Quantidade de dispositivos no sistema
     */
    public int obterQuantidadeDispositivos() {
        return listaDispositivos.size();
    }

    /**
     * @return Tarifa de energia em R$ por kWh
     */
    public double obterTarifaEnergia() {
        return tarifaEnergia;
    }

    /**
     * Define uma nova tarifa de energia.
     *
     * @param novaTarifa Nova tarifa em R$ por kWh
     */
    public void definirTarifaEnergia(double novaTarifa) {
        if (novaTarifa < 0) {
            throw new IllegalArgumentException("A tarifa de energia nao pode ser negativa.");
        }
        this.tarifaEnergia = novaTarifa;
    }
}
