package simulacao;

import util.ConstantesEnergia;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mantém o histórico de consumo de energia organizado por dia.
 */
public class HistoricoConsumo {

    private final SimpleDateFormat chaveDataFormat;
    private final Calendar dataInicial;
    private final Calendar dataAtual;
    private final Map<String, RegistroConsumoDiario> registrosPorDia;

    public HistoricoConsumo() {
        this.chaveDataFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.dataInicial = Calendar.getInstance();
        this.dataAtual = (Calendar) dataInicial.clone();
        this.registrosPorDia = new LinkedHashMap<String, RegistroConsumoDiario>();
    }

    /**
     * Registra um período de leituras no histórico, dividindo por dias conforme necessário.
     */
    public void registrarPeriodo(List<LeituraSimulada> leituras, double duracaoSegundos) {
        if (duracaoSegundos <= 0) {
            return;
        }

        double restante = duracaoSegundos;
        Calendar cursor = (Calendar) dataAtual.clone();

        while (restante > 0) {
            Calendar proximoDia = (Calendar) cursor.clone();
            proximoDia.set(Calendar.HOUR_OF_DAY, 0);
            proximoDia.set(Calendar.MINUTE, 0);
            proximoDia.set(Calendar.SECOND, 0);
            proximoDia.set(Calendar.MILLISECOND, 0);
            proximoDia.add(Calendar.DAY_OF_MONTH, 1);

            double segundosAteVirarDia = (proximoDia.getTimeInMillis() - cursor.getTimeInMillis()) / 1000.0;
            double duracaoNoDia = Math.min(restante, segundosAteVirarDia);
            long segundosRegistrados = Math.max(1, Math.round(duracaoNoDia));
            RegistroConsumoDiario registro = obterRegistroDia(cursor, true);
            registro.registrarTempoMonitorado(segundosRegistrados);

            if (leituras.isEmpty()) {
                registro.registrarEnergia("-", 0);
            } else {
                for (LeituraSimulada leitura : leituras) {
                    double energiaKWh = 0;
                    if (leitura.obterPotenciaWatts() > ConstantesEnergia.LIMITE_USO_WATTS) {
                        energiaKWh = (leitura.obterPotenciaWatts() * duracaoNoDia) / (ConstantesEnergia.SEGUNDOS_POR_HORA * 1000.0);
                    }
                    registro.registrarEnergia(leitura.obterNomeDispositivo(), energiaKWh);
                }
            }

            cursor.setTimeInMillis(cursor.getTimeInMillis() + Math.round(duracaoNoDia * 1000));
            restante -= duracaoNoDia;
        }

        dataAtual.setTimeInMillis(dataAtual.getTimeInMillis() + Math.round(duracaoSegundos * 1000));
    }

    /**
     * Obtém ou cria o registro de consumo para um dia específico.
     */
    public RegistroConsumoDiario obterRegistroDia(int ano, int indiceMes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.clear();
        calendario.set(Calendar.YEAR, ano);
        calendario.set(Calendar.MONTH, indiceMes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);
        return obterRegistroDia(calendario, false);
    }

    /**
     * Calcula a energia total consumida em um mês.
     */
    public double calcularEnergiaMes(int ano, int indiceMes) {
        double total = 0;

        for (RegistroConsumoDiario registro : registrosPorDia.values()) {
            Calendar data = registro.obterData();
            if (data.get(Calendar.YEAR) == ano && data.get(Calendar.MONTH) == indiceMes) {
                total += registro.obterEnergiaKWh();
            }
        }

        return total;
    }

    /**
     * Calcula a energia consumida por um dispositivo específico em um mês.
     */
    public double calcularEnergiaDispositivoMes(String nomeDispositivo, int ano, int indiceMes) {
        double total = 0;

        for (RegistroConsumoDiario registro : registrosPorDia.values()) {
            Calendar data = registro.obterData();
            if (data.get(Calendar.YEAR) == ano && data.get(Calendar.MONTH) == indiceMes) {
                Double energia = registro.obterEnergiaPorDispositivo().get(nomeDispositivo);
                if (energia != null) {
                    total += energia.doubleValue();
                }
            }
        }

        return total;
    }

    public long obterTempoMonitoradoMes(int ano, int indiceMes) {
        long total = 0;

        for (RegistroConsumoDiario registro : registrosPorDia.values()) {
            Calendar data = registro.obterData();
            if (data.get(Calendar.YEAR) == ano && data.get(Calendar.MONTH) == indiceMes) {
                total += registro.obterTempoMonitoradoSegundos();
            }
        }

        return total;
    }

    public double calcularEnergiaAno(int ano) {
        double total = 0;

        for (RegistroConsumoDiario registro : registrosPorDia.values()) {
            Calendar data = registro.obterData();
            if (data.get(Calendar.YEAR) == ano) {
                total += registro.obterEnergiaKWh();
            }
        }

        return total;
    }

    public double calcularMediaMensalAno(int ano) {
        double total = 0;
        int mesesComLeitura = 0;

        for (int mes = 0; mes < 12; mes++) {
            double energiaMes = calcularEnergiaMes(ano, mes);
            if (energiaMes > 0) {
                total += energiaMes;
                mesesComLeitura++;
            }
        }

        return mesesComLeitura == 0 ? 0 : total / mesesComLeitura;
    }

    public double calcularMediaDiariaMes(int ano, int indiceMes, int diasMes) {
        double total = 0;
        int diasComLeitura = 0;

        for (int dia = 1; dia <= diasMes; dia++) {
            RegistroConsumoDiario registro = obterRegistroDia(ano, indiceMes, dia);
            if (registro != null && registro.obterEnergiaKWh() > 0) {
                total += registro.obterEnergiaKWh();
                diasComLeitura++;
            }
        }

        return diasComLeitura == 0 ? 0 : total / diasComLeitura;
    }

    public Calendar obterDataInicial() {
        return (Calendar) dataInicial.clone();
    }

    public Calendar obterDataAtual() {
        return (Calendar) dataAtual.clone();
    }

    public int obterMesAtual() {
        return dataAtual.get(Calendar.MONTH);
    }

    public int obterAnoAtual() {
        return dataAtual.get(Calendar.YEAR);
    }

    public void zerar() {
        registrosPorDia.clear();
        dataInicial.setTime(Calendar.getInstance().getTime());
        dataAtual.setTime(dataInicial.getTime());
    }

    private RegistroConsumoDiario obterRegistroDia(Calendar data, boolean criar) {
        String chave = chaveDataFormat.format(data.getTime());
        RegistroConsumoDiario registro = registrosPorDia.get(chave);

        if (registro == null && criar) {
            Calendar dataRegistro = (Calendar) data.clone();
            dataRegistro.set(Calendar.HOUR_OF_DAY, 0);
            dataRegistro.set(Calendar.MINUTE, 0);
            dataRegistro.set(Calendar.SECOND, 0);
            dataRegistro.set(Calendar.MILLISECOND, 0);

            registro = new RegistroConsumoDiario(dataRegistro);
            registrosPorDia.put(chave, registro);
        }

        return registro;
    }
}
