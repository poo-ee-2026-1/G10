package sistema;

import dispositivos.DispositivoEletrico;
import java.util.ArrayList;
import java.util.List;

public class SistemaEnergetico {

    private List<DispositivoEletrico> listaDispositivos;
    private double tarifaEnergia; // em R$ por kWh

    public SistemaEnergetico(double tarifaEnergia) {
        this.listaDispositivos = new ArrayList<>();
        this.tarifaEnergia = tarifaEnergia;
    }

    public void adicionarDispositivo(DispositivoEletrico dispositivo) {
        listaDispositivos.add(dispositivo);
        System.out.println("✓ Dispositivo '" + dispositivo.getNome() + "' adicionado ao sistema.");
    }

    public void removerDispositivo(DispositivoEletrico dispositivo) {
        listaDispositivos.remove(dispositivo);
        System.out.println("✓ Dispositivo '" + dispositivo.getNome() + "' removido do sistema.");
    }

    public double calcularConsumoDiario() {
        double consumoTotal = 0;
        for (DispositivoEletrico dispositivo : listaDispositivos) {
            consumoTotal += dispositivo.calcularConsumoDiario();
        }
        return consumoTotal;
    }

    public double calcularConsumoMensal() {
        double consumoTotal = 0;
        for (DispositivoEletrico dispositivo : listaDispositivos) {
            consumoTotal += dispositivo.calcularConsumoMensal();
        }
        return consumoTotal;
    }

    public double calcularCustoDiario() {
        return calcularConsumoDiario() * tarifaEnergia;
    }

    public double calcularCustoMensal() {
        return calcularConsumoMensal() * tarifaEnergia;
    }

    public void exibirResumo() {
        System.out.println("\n========== RESUMO DO SISTEMA ENERGÉTICO ==========");
        System.out.println("Tarifa de energia: R$ " + String.format("%.2f", tarifaEnergia) + " por kWh");
        System.out.println("\n--- Dispositivos ---");
        
        for (DispositivoEletrico dispositivo : listaDispositivos) {
            System.out.println("\n• " + dispositivo.getNome());
            System.out.println("  Potência: " + dispositivo.getPotenciaWatts() + " W");
            System.out.println("  Tempo coletado: " + dispositivo.obterTempoUsoSegundos() + " s");
            System.out.println("  Energia coletada: " + String.format("%.4f", dispositivo.calcularConsumoDiario()) + " kWh");
            System.out.println("  Projeção mensal: " + String.format("%.2f", dispositivo.calcularConsumoMensal()) + " kWh");
        }

        System.out.println("\n--- Totais ---");
        System.out.println("Energia coletada total: " + String.format("%.4f", calcularConsumoDiario()) + " kWh");
        System.out.println("Projeção mensal total: " + String.format("%.2f", calcularConsumoMensal()) + " kWh");
        System.out.println("Custo coletado: R$ " + String.format("%.2f", calcularCustoDiario()));
        System.out.println("Custo mensal projetado: R$ " + String.format("%.2f", calcularCustoMensal()));
        System.out.println("===================================================\n");
    }

    public List<DispositivoEletrico> obterDispositivos() {
        return listaDispositivos;
    }

    public int obterQuantidadeDispositivos() {
        return listaDispositivos.size();
    }

    public double obterTarifaEnergia() {
        return tarifaEnergia;
    }

    public void definirTarifaEnergia(double novaTarifa) {
        this.tarifaEnergia = novaTarifa;
    }
}
