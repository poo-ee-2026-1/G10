package gui;

import dispositivos.DispositivoEletrico;
import sensores.SensorEnergia;
import sistema.SistemaEnergetico;
import simulacao.HistoricoConsumo;
import simulacao.LeituraSimulada;
import simulacao.RegistroConsumoDiario;
import simulacao.SimuladorEnergia;
import util.ConstantesEnergia;
import util.FormatadorEnergia;
import util.TratadorExcecoes;
import util.ValidadorEntrada;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class SistemaEnergiaGUI {

    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    private static final double TENSAO_PADRAO = ConstantesEnergia.TENSAO_PADRAO_VOLT;
    private static final double LIMITE_ALERTA_WATTS = ConstantesEnergia.LIMITE_ALERTA_POTENCIA_WATTS;
    private static final double LIMITE_USO_WATTS = ConstantesEnergia.LIMITE_USO_WATTS;
    private static final int INTERVALO_ATUALIZACAO_MS = ConstantesEnergia.INTERVALO_ATUALIZACAO_MS;
    private static final int MESES_ANO = ConstantesEnergia.MESES_POR_ANO;
    private static final Color COR_FUNDO = new Color(15, 18, 24);
    private static final Color COR_PAINEL = new Color(25, 30, 39);
    private static final Color COR_PAINEL_ELEVADO = new Color(32, 39, 50);
    private static final Color COR_CAMPO = new Color(18, 22, 29);
    private static final Color COR_PRIMARIA = new Color(71, 141, 216);
    private static final Color COR_TEXTO = new Color(232, 237, 245);
    private static final Color COR_TEXTO_SUAVE = new Color(164, 174, 190);
    private static final Color COR_BORDA = new Color(58, 68, 84);
    private static final Color COR_GRADE = new Color(44, 53, 67);

    private final SistemaEnergetico sistema;
    private final NumberFormat moedaFormat;
    private final SimpleDateFormat horarioFormat;
    private final SimpleDateFormat dataHoraFormat;
    private final SimpleDateFormat dataSimuladaFormat;
    private final List<Double> historicoPotenciaTempoReal;
    private final HistoricoConsumo historicoConsumo;
    private final SimuladorEnergia simuladorEnergia;

    private JFrame janela;
    private JTextField campoNome;
    private JTextField campoPotencia;
    private JTextField campoTarifa;
    private JComboBox<OpcaoSimulacao> comboEscalaSimulacao;
    private JComboBox<String> comboMesGrafico;
    private JComboBox<Integer> comboAnoGrafico;
    private DefaultTableModel modeloTabela;
    private DefaultTableModel modeloMonitoramento;
    private JTable tabelaDispositivos;
    private JTable tabelaMonitoramento;
    private DefaultTableModel modeloDetalhesCalendario;
    private JTable tabelaDetalhesCalendario;
    private JTextArea areaLeituras;
    private JTextArea areaRelatorioMensal;
    private JTextArea areaAnaliseCalendario;
    private JButton[] botoesMesesCalendario;
    private JComboBox<Integer> comboAnoCalendario;
    private JLabel labelQuantidade;
    private JLabel labelConsumoDiario;
    private JLabel labelConsumoMensal;
    private JLabel labelCustoDiario;
    private JLabel labelCustoMensal;
    private JLabel labelStatusMonitoramento;
    private JLabel labelPotenciaAtual;
    private JLabel labelCustoHoraAtual;
    private JLabel labelEscalaMonitoramento;
    private JLabel labelResumoGraficos;
    private JLabel labelResumoCalendario;
    private JLabel labelMesSelecionadoCalendario;
    private JLabel labelStatusTarifa;
    private GraficoConsumoMensalPanel graficoConsumoMensal;
    private GraficoConsumoDiarioPanel graficoConsumoDiario;
    private GraficoPotenciaTempoRealPanel graficoPotenciaTempoReal;
    private Timer timerMonitoramento;
    private int contadorLeiturasTempoReal;
    private int mesSelecionadoCalendario;

    public SistemaEnergiaGUI() {
        this.sistema = new SistemaEnergetico(0.75);
        this.moedaFormat = NumberFormat.getCurrencyInstance(LOCALE_BR);
        this.horarioFormat = new SimpleDateFormat("HH:mm:ss");
        this.dataHoraFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.dataSimuladaFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.historicoPotenciaTempoReal = new ArrayList<Double>();
        this.historicoConsumo = new HistoricoConsumo();
        this.simuladorEnergia = new SimuladorEnergia();
        this.mesSelecionadoCalendario = Calendar.getInstance().get(Calendar.MONTH);
    }

    public void exibir() {
        configurarAparencia();
        carregarDispositivosIniciais();

        janela = new JFrame("Sistema de Monitoramento de Energia");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setMinimumSize(new Dimension(980, 620));
        janela.setContentPane(criarConteudo());
        aplicarTemaEscuro(janela.getContentPane());
        janela.pack();
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);

        atualizarTela();
    }

    private void configurarAparencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception excecao) {
            TratadorExcecoes.logErro("configurar aparencia", "Look and feel indisponivel", excecao);
            // Mantem o visual padrao caso o sistema nao aceite o tema nativo.
        }

        UIManager.put("Panel.background", COR_FUNDO);
        UIManager.put("Label.foreground", COR_TEXTO);
        UIManager.put("TabbedPane.background", COR_FUNDO);
        UIManager.put("TabbedPane.foreground", COR_TEXTO);
        UIManager.put("TabbedPane.selected", COR_PAINEL_ELEVADO);
        UIManager.put("Table.background", COR_CAMPO);
        UIManager.put("Table.foreground", COR_TEXTO);
        UIManager.put("Table.gridColor", COR_GRADE);
        UIManager.put("Table.selectionBackground", COR_PRIMARIA);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", COR_PAINEL_ELEVADO);
        UIManager.put("TableHeader.foreground", COR_TEXTO);
        UIManager.put("TextField.background", COR_CAMPO);
        UIManager.put("TextField.foreground", COR_TEXTO);
        UIManager.put("TextField.caretForeground", COR_TEXTO);
        UIManager.put("TextArea.background", COR_CAMPO);
        UIManager.put("TextArea.foreground", COR_TEXTO);
        UIManager.put("ComboBox.background", COR_CAMPO);
        UIManager.put("ComboBox.foreground", COR_TEXTO);
        UIManager.put("ScrollPane.background", COR_PAINEL);
        UIManager.put("Viewport.background", COR_CAMPO);
        UIManager.put("Button.background", COR_PAINEL_ELEVADO);
        UIManager.put("Button.foreground", COR_TEXTO);
        UIManager.put("Button.select", COR_PRIMARIA);
        UIManager.put("Button.focus", COR_BORDA);
    }

    private JPanel criarConteudo() {
        JPanel raiz = new JPanel(new BorderLayout(12, 12));
        raiz.setBorder(new EmptyBorder(16, 16, 16, 16));

        raiz.add(criarCabecalho(), BorderLayout.NORTH);

        JSplitPane divisor = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                criarPainelControles(),
                criarPainelPrincipal());
        divisor.setResizeWeight(0.24);
        divisor.setBorder(null);
        divisor.setDividerSize(8);

        raiz.add(divisor, BorderLayout.CENTER);
        raiz.setBackground(COR_FUNDO);
        return raiz;
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(true);
        painel.setBackground(COR_PRIMARIA);
        painel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel titulo = new JLabel("Sistema de Monitoramento de Energia");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Consumo, custo, graficos e calendario em memoria");
        subtitulo.setForeground(new Color(219, 231, 246));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        painel.add(textos, BorderLayout.WEST);
        return painel;
    }

    private JPanel criarPainelControles() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);

        painel.add(criarPainelTarifa());
        painel.add(Box.createVerticalStrut(12));
        painel.add(criarFormularioDispositivo());

        return painel;
    }

    private JPanel criarPainelTarifa() {
        JPanel painel = criarPainelComTitulo("Tarifa");
        painel.setLayout(new BorderLayout(8, 8));

        campoTarifa = new JTextField("0,75");
        estilizarCampo(campoTarifa);
        campoTarifa.addActionListener(evento -> atualizarTarifa());
        JButton botaoAtualizar = new JButton("Atualizar");
        estilizarBotaoPrimario(botaoAtualizar);
        botaoAtualizar.addActionListener(evento -> atualizarTarifa());
        labelStatusTarifa = criarRotuloCampo("Tarifa pronta");

        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.add(criarRotuloCampo("R$/kWh"), BorderLayout.WEST);
        linha.add(campoTarifa, BorderLayout.CENTER);
        linha.add(botaoAtualizar, BorderLayout.EAST);

        painel.add(linha, BorderLayout.CENTER);
        painel.add(labelStatusTarifa, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarFormularioDispositivo() {
        JPanel painel = criarPainelComTitulo("Dispositivo");
        painel.setLayout(new BorderLayout(8, 8));

        campoNome = new JTextField();
        campoPotencia = new JTextField();
        estilizarCampo(campoNome);
        estilizarCampo(campoPotencia);
        campoNome.addActionListener(evento -> campoPotencia.requestFocusInWindow());
        campoPotencia.addActionListener(evento -> adicionarDispositivo());

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        adicionarCampo(formulario, "Nome", campoNome, 0);
        adicionarCampo(formulario, "Potencia (W)", campoPotencia, 1);

        JButton botaoAdicionar = new JButton("Adicionar");
        estilizarBotaoPrimario(botaoAdicionar);
        botaoAdicionar.addActionListener(evento -> adicionarDispositivo());

        JButton botaoRemover = new JButton("Remover selecionado");
        estilizarBotaoSecundario(botaoRemover);
        botaoRemover.addActionListener(evento -> removerDispositivoSelecionado());

        JButton botaoLimpar = new JButton("Limpar campos");
        estilizarBotaoSecundario(botaoLimpar);
        botaoLimpar.addActionListener(evento -> limparCamposDispositivo());

        JPanel botoes = new JPanel(new GridLayout(3, 1, 0, 8));
        botoes.add(botaoAdicionar);
        botoes.add(botaoRemover);
        botoes.add(botaoLimpar);

        painel.add(formulario, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelPrincipal() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(COR_FUNDO);
        painel.add(criarPainelResumo(), BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Dispositivos", criarPainelTabela());
        abas.addTab("Tempo real", criarPainelMonitoramento());
        abas.addTab("Graficos", criarPainelGraficos());
        abas.addTab("Calendario", criarPainelCalendarioMensal());
        abas.addTab("Historico", criarPainelLeituras());
        abas.addTab("Relatorio", criarPainelRelatorioMensal());
        estilizarAbas(abas);

        painel.add(abas, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelResumo() {
        JPanel painel = criarPainelComTitulo("Resumo");
        painel.setLayout(new GridLayout(1, 5, 8, 8));

        labelQuantidade = criarCartaoResumo("Dispositivos", "0");
        labelConsumoDiario = criarCartaoResumo("Energia coletada", "0,0000 kWh");
        labelConsumoMensal = criarCartaoResumo("Proj. mensal", "0,0000 kWh");
        labelCustoDiario = criarCartaoResumo("Custo coletado", "R$ 0,00");
        labelCustoMensal = criarCartaoResumo("Custo proj.", "R$ 0,00");

        painel.add(labelQuantidade);
        painel.add(labelConsumoDiario);
        painel.add(labelConsumoMensal);
        painel.add(labelCustoDiario);
        painel.add(labelCustoMensal);

        return painel;
    }

    private JPanel criarPainelTabela() {
        modeloTabela = new DefaultTableModel(
                new String[] {
                    "Nome",
                    "Potencia (W)",
                    "Tempo coletado",
                    "Energia coletada",
                    "Proj. mensal",
                    "Custo proj."
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaDispositivos = new JTable(modeloTabela);
        tabelaDispositivos.setFillsViewportHeight(true);
        tabelaDispositivos.setRowHeight(24);
        tabelaDispositivos.setAutoCreateRowSorter(true);
        estilizarTabela(tabelaDispositivos);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_PAINEL);
        painel.add(new JScrollPane(tabelaDispositivos), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelMonitoramento() {
        modeloMonitoramento = new DefaultTableModel(
                new String[] {
                    "Horario",
                    "Periodo simulado",
                    "Dispositivo",
                    "Tensao (V)",
                    "Corrente (A)",
                    "Potencia medida (W)",
                    "Status"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaMonitoramento = new JTable(modeloMonitoramento);
        tabelaMonitoramento.setFillsViewportHeight(true);
        tabelaMonitoramento.setRowHeight(24);
        tabelaMonitoramento.setAutoCreateRowSorter(true);
        estilizarTabela(tabelaMonitoramento);

        JButton botaoIniciar = new JButton("Iniciar");
        estilizarBotaoPrimario(botaoIniciar);
        botaoIniciar.addActionListener(evento -> iniciarMonitoramento());

        JButton botaoParar = new JButton("Parar");
        estilizarBotaoSecundario(botaoParar);
        botaoParar.addActionListener(evento -> pararMonitoramento());

        labelStatusMonitoramento = new JLabel("Parado");
        labelPotenciaAtual = new JLabel("Potencia atual: 0,00 W");
        labelCustoHoraAtual = new JLabel("Custo da leitura: R$ 0,00");
        labelEscalaMonitoramento = new JLabel();
        labelStatusMonitoramento.setForeground(COR_TEXTO_SUAVE);
        labelEscalaMonitoramento.setForeground(COR_TEXTO_SUAVE);

        comboEscalaSimulacao = new JComboBox<OpcaoSimulacao>(new OpcaoSimulacao[] {
            new OpcaoSimulacao("1 segundo", 1),
            new OpcaoSimulacao("1 minuto", 60),
            new OpcaoSimulacao("1 hora", 3600),
            new OpcaoSimulacao("1 dia", 86400)
        });
        estilizarCombo(comboEscalaSimulacao);
        comboEscalaSimulacao.addActionListener(evento -> {
            atualizarTextoEscalaSimulacao();
            atualizarMonitoramentoSeParado();
        });

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controles.add(botaoIniciar);
        controles.add(botaoParar);
        controles.add(criarRotuloCampo("Cada leitura simula:"));
        controles.add(comboEscalaSimulacao);
        controles.add(labelEscalaMonitoramento);
        controles.add(labelStatusMonitoramento);

        JPanel indicadores = new JPanel(new GridLayout(1, 2, 8, 0));
        indicadores.add(criarIndicadorTempoReal(labelPotenciaAtual));
        indicadores.add(criarIndicadorTempoReal(labelCustoHoraAtual));

        JPanel topo = new JPanel(new BorderLayout(8, 8));
        topo.add(controles, BorderLayout.NORTH);
        topo.add(indicadores, BorderLayout.CENTER);

        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaMonitoramento), BorderLayout.CENTER);

        atualizarTextoEscalaSimulacao();
        timerMonitoramento = new Timer(INTERVALO_ATUALIZACAO_MS, evento -> atualizarMonitoramentoTempoReal());
        return painel;
    }

    private JPanel criarPainelGraficos() {
        graficoConsumoMensal = new GraficoConsumoMensalPanel();
        graficoConsumoDiario = new GraficoConsumoDiarioPanel();
        graficoPotenciaTempoReal = new GraficoPotenciaTempoRealPanel();

        labelResumoGraficos = new JLabel("Graficos prontos para atualizacao");
        labelResumoGraficos.setForeground(COR_TEXTO_SUAVE);

        JButton botaoAtualizar = new JButton("Atualizar graficos");
        estilizarBotaoSecundario(botaoAtualizar);
        botaoAtualizar.addActionListener(evento -> atualizarGraficos());

        JButton botaoLimparHistorico = new JButton("Limpar historico");
        estilizarBotaoSecundario(botaoLimparHistorico);
        botaoLimparHistorico.addActionListener(evento -> limparHistoricoGraficoTempoReal());

        comboMesGrafico = new JComboBox<String>();
        for (int mes = 0; mes < MESES_ANO; mes++) {
            comboMesGrafico.addItem(obterNomeMes(mes));
        }
        comboMesGrafico.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        estilizarCombo(comboMesGrafico);
        comboMesGrafico.addActionListener(evento -> atualizarGraficos());

        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        comboAnoGrafico = new JComboBox<Integer>();
        for (int ano = anoAtual - 2; ano <= anoAtual + 4; ano++) {
            comboAnoGrafico.addItem(Integer.valueOf(ano));
        }
        comboAnoGrafico.setSelectedItem(Integer.valueOf(anoAtual));
        estilizarCombo(comboAnoGrafico);
        comboAnoGrafico.addActionListener(evento -> atualizarGraficos());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.add(criarRotuloCampo("Mes:"));
        barra.add(comboMesGrafico);
        barra.add(criarRotuloCampo("Ano:"));
        barra.add(comboAnoGrafico);
        barra.add(botaoAtualizar);
        barra.add(botaoLimparHistorico);
        barra.add(labelResumoGraficos);

        JPanel gradeGraficos = new JPanel(new GridLayout(3, 1, 0, 10));
        gradeGraficos.setBackground(COR_FUNDO);
        gradeGraficos.add(graficoConsumoMensal);
        gradeGraficos.add(graficoConsumoDiario);
        gradeGraficos.add(graficoPotenciaTempoReal);

        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.add(barra, BorderLayout.NORTH);
        painel.add(new JScrollPane(gradeGraficos), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelCalendarioMensal() {
        botoesMesesCalendario = new JButton[MESES_ANO];

        JPanel grade = new JPanel(new GridLayout(3, 4, 8, 8));
        grade.setBackground(COR_FUNDO);
        for (int i = 0; i < MESES_ANO; i++) {
            final int mes = i;
            JButton botaoMes = new JButton();
            botaoMes.setUI(new BasicButtonUI());
            botaoMes.setHorizontalAlignment(SwingConstants.CENTER);
            botaoMes.setVerticalAlignment(SwingConstants.CENTER);
            botaoMes.setOpaque(true);
            botaoMes.setContentAreaFilled(false);
            botaoMes.setFocusPainted(false);
            botaoMes.addActionListener(evento -> {
                mesSelecionadoCalendario = mes;
                atualizarCalendarioMensal();
            });
            botaoMes.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    if (evento.getClickCount() == 2) {
                        mesSelecionadoCalendario = mes;
                        atualizarCalendarioMensal();
                        abrirAnaliseDiariaMes(mes);
                    }
                }
            });
            botoesMesesCalendario[i] = botaoMes;
            grade.add(botaoMes);
        }

        labelResumoCalendario = new JLabel("Calendario aguardando leituras");
        labelResumoCalendario.setForeground(COR_TEXTO_SUAVE);

        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        comboAnoCalendario = new JComboBox<Integer>();
        for (int ano = anoAtual - 2; ano <= anoAtual + 4; ano++) {
            comboAnoCalendario.addItem(Integer.valueOf(ano));
        }
        comboAnoCalendario.setSelectedItem(Integer.valueOf(anoAtual));
        estilizarCombo(comboAnoCalendario);
        comboAnoCalendario.addActionListener(evento -> atualizarCalendarioMensal());

        JButton botaoAtualizar = new JButton("Atualizar calendario");
        estilizarBotaoSecundario(botaoAtualizar);
        botaoAtualizar.addActionListener(evento -> atualizarCalendarioMensal());

        JButton botaoZerar = new JButton("Zerar coletas");
        estilizarBotaoSecundario(botaoZerar);
        botaoZerar.addActionListener(evento -> zerarColetasMensais());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.add(criarRotuloCampo("Ano:"));
        barra.add(comboAnoCalendario);
        barra.add(botaoAtualizar);
        barra.add(botaoZerar);
        barra.add(labelResumoCalendario);

        JPanel painelCalendario = new JPanel(new BorderLayout(8, 8));
        painelCalendario.add(barra, BorderLayout.NORTH);
        painelCalendario.add(grade, BorderLayout.CENTER);

        modeloDetalhesCalendario = new DefaultTableModel(
                new String[] {
                    "Dispositivo",
                    "Energia coletada",
                    "Custo estimado",
                    "Participacao"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaDetalhesCalendario = new JTable(modeloDetalhesCalendario);
        tabelaDetalhesCalendario.setFillsViewportHeight(true);
        tabelaDetalhesCalendario.setRowHeight(24);
        tabelaDetalhesCalendario.setAutoCreateRowSorter(true);
        estilizarTabela(tabelaDetalhesCalendario);

        labelMesSelecionadoCalendario = new JLabel("Mes");
        labelMesSelecionadoCalendario.setFont(labelMesSelecionadoCalendario.getFont().deriveFont(Font.BOLD, 14f));
        labelMesSelecionadoCalendario.setForeground(COR_TEXTO);

        areaAnaliseCalendario = new JTextArea();
        areaAnaliseCalendario.setEditable(false);
        areaAnaliseCalendario.setLineWrap(true);
        areaAnaliseCalendario.setWrapStyleWord(true);
        areaAnaliseCalendario.setRows(8);
        estilizarAreaTexto(areaAnaliseCalendario);

        JPanel painelDetalhes = new JPanel(new BorderLayout(8, 8));
        painelDetalhes.add(labelMesSelecionadoCalendario, BorderLayout.NORTH);
        painelDetalhes.add(new JScrollPane(tabelaDetalhesCalendario), BorderLayout.CENTER);
        painelDetalhes.add(new JScrollPane(areaAnaliseCalendario), BorderLayout.SOUTH);

        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelCalendario, painelDetalhes);
        divisor.setResizeWeight(0.55);
        divisor.setBorder(null);

        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.add(divisor, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelLeituras() {
        areaLeituras = new JTextArea();
        areaLeituras.setEditable(false);
        areaLeituras.setLineWrap(true);
        areaLeituras.setWrapStyleWord(true);
        estilizarAreaTexto(areaLeituras);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(COR_PAINEL);
        painel.add(new JScrollPane(areaLeituras), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelRelatorioMensal() {
        areaRelatorioMensal = new JTextArea();
        areaRelatorioMensal.setEditable(false);
        areaRelatorioMensal.setLineWrap(true);
        areaRelatorioMensal.setWrapStyleWord(true);
        areaRelatorioMensal.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        estilizarAreaTexto(areaRelatorioMensal);

        JButton botaoAtualizar = new JButton("Atualizar relatorio");
        estilizarBotaoSecundario(botaoAtualizar);
        botaoAtualizar.addActionListener(evento -> atualizarRelatorioMensal());

        JButton botaoExportar = new JButton("Exportar TXT");
        estilizarBotaoPrimario(botaoExportar);
        botaoExportar.addActionListener(evento -> exportarRelatorioMensal());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.add(botaoAtualizar);
        botoes.add(botaoExportar);

        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.add(botoes, BorderLayout.NORTH);
        painel.add(new JScrollPane(areaRelatorioMensal), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelComTitulo(String titulo) {
        JPanel painel = new JPanel();
        painel.setBackground(COR_PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
                criarBordaTituloEscura(titulo),
                new EmptyBorder(10, 10, 10, 10)));
        return painel;
    }

    private TitledBorder criarBordaTituloEscura(String titulo) {
        TitledBorder bordaTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                titulo);
        bordaTitulo.setTitleColor(COR_TEXTO_SUAVE);
        return bordaTitulo;
    }

    private JLabel criarCartaoResumo(String titulo, String valor) {
        JLabel label = new JLabel(criarTextoResumo(titulo, valor), SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(COR_PAINEL);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                new EmptyBorder(10, 8, 10, 8)));
        return label;
    }

    private JLabel criarIndicadorTempoReal(JLabel label) {
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBackground(COR_PAINEL);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                new EmptyBorder(10, 8, 10, 8)));
        return label;
    }

    private String criarTextoResumo(String titulo, String valor) {
        return "<html><body style='text-align:center'>"
                + "<div style='font-size:10px;color:#a4aebe'>" + titulo + "</div>"
                + "<div style='font-size:16px;font-weight:bold;color:#e8edf5'>" + valor + "</div>"
                + "</body></html>";
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(COR_CAMPO);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                new EmptyBorder(5, 7, 5, 7)));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setUI(new BasicComboBoxUI());
        combo.setBackground(COR_CAMPO);
        combo.setForeground(COR_TEXTO);
        combo.setOpaque(true);
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                Component componente = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                componente.setBackground(isSelected ? COR_PRIMARIA : COR_CAMPO);
                componente.setForeground(COR_TEXTO);
                return componente;
            }
        });
    }

    private void estilizarAbas(final JTabbedPane abas) {
        abas.setOpaque(true);
        abas.setBackground(COR_FUNDO);
        abas.setForeground(COR_TEXTO_SUAVE);
        abas.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        abas.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(
                    Graphics graphics,
                    int tabPlacement,
                    int tabIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    boolean isSelected) {
                graphics.setColor(isSelected ? COR_PAINEL_ELEVADO : COR_CAMPO);
                graphics.fillRect(x, y, width, height);
            }

            @Override
            protected void paintContentBorder(Graphics graphics, int tabPlacement, int selectedIndex) {
                graphics.setColor(COR_BORDA);
                graphics.drawRect(0, 0, abas.getWidth() - 1, abas.getHeight() - 1);
            }
        });

        for (int i = 0; i < abas.getTabCount(); i++) {
            abas.setBackgroundAt(i, i == abas.getSelectedIndex() ? COR_PAINEL_ELEVADO : COR_CAMPO);
            abas.setForegroundAt(i, COR_TEXTO_SUAVE);
        }
    }

    private void estilizarAreaTexto(JTextArea area) {
        area.setBackground(COR_CAMPO);
        area.setForeground(COR_TEXTO);
        area.setCaretColor(COR_TEXTO);
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setBackground(COR_CAMPO);
        tabela.setForeground(COR_TEXTO);
        tabela.setGridColor(COR_GRADE);
        tabela.setSelectionBackground(COR_PRIMARIA);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.getTableHeader().setBackground(COR_PAINEL_ELEVADO);
        tabela.getTableHeader().setForeground(COR_TEXTO);
        tabela.getTableHeader().setBorder(BorderFactory.createLineBorder(COR_BORDA));
        tabela.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component componente = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                componente.setBackground(COR_PAINEL_ELEVADO);
                componente.setForeground(COR_TEXTO);
                setBorder(BorderFactory.createLineBorder(COR_BORDA));
                return componente;
            }
        });
    }

    private void estilizarBotaoPrimario(JButton botao) {
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_PRIMARIA),
                new EmptyBorder(6, 12, 6, 12)));
    }

    private void estilizarBotaoSecundario(JButton botao) {
        botao.setBackground(COR_PAINEL_ELEVADO);
        botao.setForeground(COR_TEXTO);
        botao.setOpaque(true);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                new EmptyBorder(6, 12, 6, 12)));
    }

    private void aplicarTemaEscuro(Component componente) {
        if (componente instanceof JPanel) {
            if (Color.WHITE.equals(componente.getBackground())) {
                componente.setBackground(COR_FUNDO);
            }
        } else if (componente instanceof JTextField) {
            estilizarCampo((JTextField) componente);
        } else if (componente instanceof JTextArea) {
            estilizarAreaTexto((JTextArea) componente);
        } else if (componente instanceof JTable) {
            estilizarTabela((JTable) componente);
        } else if (componente instanceof JComboBox<?>) {
            estilizarCombo((JComboBox<?>) componente);
        } else if (componente instanceof JScrollPane) {
            componente.setBackground(COR_PAINEL);
            ((JScrollPane) componente).getViewport().setBackground(COR_CAMPO);
        } else if (componente instanceof JViewport) {
            componente.setBackground(COR_CAMPO);
        } else if (componente instanceof JButton) {
            JButton botao = (JButton) componente;
            if (botao.getBackground() == null || Color.WHITE.equals(botao.getBackground())) {
                estilizarBotaoSecundario(botao);
            }
        } else if (componente instanceof JLabel) {
            JLabel label = (JLabel) componente;
            if (!Color.WHITE.equals(label.getForeground())) {
                label.setForeground(COR_TEXTO_SUAVE);
            }
        }

        if (componente instanceof Container) {
            Component[] filhos = ((Container) componente).getComponents();
            for (Component filho : filhos) {
                aplicarTemaEscuro(filho);
            }
        }
    }

    private void adicionarCampo(JPanel painel, String rotulo, Component campo, int linha) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = linha;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(4, 0, 4, 8);

        GridBagConstraints campoConstraints = new GridBagConstraints();
        campoConstraints.gridx = 1;
        campoConstraints.gridy = linha;
        campoConstraints.weightx = 1;
        campoConstraints.fill = GridBagConstraints.HORIZONTAL;
        campoConstraints.insets = new Insets(4, 0, 4, 0);

        painel.add(criarRotuloCampo(rotulo), labelConstraints);
        painel.add(campo, campoConstraints);
    }

    private JLabel criarRotuloCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(COR_TEXTO_SUAVE);
        return label;
    }

    private void carregarDispositivosIniciais() {
        sistema.adicionarDispositivo(new DispositivoEletrico("Lampada LED", 60));
        sistema.adicionarDispositivo(new DispositivoEletrico("Geladeira", 500));
        sistema.adicionarDispositivo(new DispositivoEletrico("Chuveiro Eletrico", 5500));
        sistema.adicionarDispositivo(new DispositivoEletrico("Televisao", 150));
    }

    private void adicionarDispositivo() {
        try {
            String nome = campoNome.getText().trim();
            if (!ValidadorEntrada.validarNomeDispositivo(nome)) {
                throw new IllegalArgumentException(ValidadorEntrada.obterMensagemErro("Nome", "nome"));
            }
            if (existeDispositivoComNome(nome)) {
                throw new IllegalArgumentException("Ja existe um dispositivo cadastrado com esse nome.");
            }

            double potencia = lerNumero(campoPotencia, "potencia");
            if (!ValidadorEntrada.validarPotencia(normalizarNumero(campoPotencia.getText()))) {
                throw new IllegalArgumentException(ValidadorEntrada.obterMensagemErro("Potencia", "potencia"));
            }

            DispositivoEletrico dispositivo = new DispositivoEletrico(nome, potencia);
            sistema.adicionarDispositivo(dispositivo);

            campoNome.setText("");
            campoPotencia.setText("");

            atualizarTela();
            atualizarMonitoramentoSeParado();
            selecionarDispositivoNaTabela(dispositivo);
            campoNome.requestFocusInWindow();
        } catch (IllegalArgumentException excecao) {
            mostrarErro(excecao.getMessage());
        }
    }

    private boolean existeDispositivoComNome(String nome) {
        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            if (dispositivo.getNome().equalsIgnoreCase(nome.trim())) {
                return true;
            }
        }
        return false;
    }

    private void limparCamposDispositivo() {
        campoNome.setText("");
        campoPotencia.setText("");
        tabelaDispositivos.clearSelection();
        campoNome.requestFocusInWindow();
    }

    private void selecionarDispositivoNaTabela(DispositivoEletrico dispositivo) {
        int indiceModelo = sistema.obterDispositivos().indexOf(dispositivo);
        if (indiceModelo < 0 || tabelaDispositivos == null) {
            return;
        }

        int indiceVisual = tabelaDispositivos.convertRowIndexToView(indiceModelo);
        tabelaDispositivos.setRowSelectionInterval(indiceVisual, indiceVisual);
        tabelaDispositivos.scrollRectToVisible(tabelaDispositivos.getCellRect(indiceVisual, 0, true));
    }

    private void removerDispositivoSelecionado() {
        int linhaSelecionada = tabelaDispositivos.getSelectedRow();
        if (linhaSelecionada < 0) {
            mostrarErro("Selecione um dispositivo na tabela.");
            return;
        }

        int indiceModelo = tabelaDispositivos.convertRowIndexToModel(linhaSelecionada);
        DispositivoEletrico dispositivo = sistema.obterDispositivos().get(indiceModelo);

        int resposta = JOptionPane.showConfirmDialog(
                janela,
                "Remover \"" + dispositivo.getNome() + "\" do sistema?",
                "Confirmar remocao",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            sistema.removerDispositivo(dispositivo);
            atualizarTela();
            atualizarMonitoramentoSeParado();
        }
    }

    private void atualizarTarifa() {
        try {
            double tarifa = lerNumero(campoTarifa, "tarifa");
            if (!ValidadorEntrada.validarNumeroNaoNegativo(normalizarNumero(campoTarifa.getText()))) {
                throw new IllegalArgumentException(ValidadorEntrada.obterMensagemErro("Tarifa", "naoNegativo"));
            }

            sistema.definirTarifaEnergia(tarifa);
            if (labelStatusTarifa != null) {
                labelStatusTarifa.setText("Tarifa atualizada: " + moedaFormat.format(tarifa) + " por kWh");
                labelStatusTarifa.setForeground(new Color(122, 216, 161));
            }
            atualizarTela();
            atualizarMonitoramentoSeParado();
        } catch (IllegalArgumentException excecao) {
            mostrarErro(excecao.getMessage());
        }
    }

    private void atualizarTela() {
        atualizarTabela();
        atualizarResumo();
        atualizarRelatorioMensal();
        atualizarMonitoramentoSeParado();
        atualizarCalendarioMensal();
        campoTarifa.setText(formatarNumero(sistema.obterTarifaEnergia()));
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);

        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            double custoMensal = dispositivo.projetarConsumoMensalKWh() * sistema.obterTarifaEnergia();

            modeloTabela.addRow(new Object[] {
                dispositivo.getNome(),
                formatarNumero(dispositivo.getPotenciaWatts()),
                formatarTempo(dispositivo.obterTempoUsoSegundos()),
                formatarEnergia(dispositivo.obterEnergiaColetadaKWh()) + " kWh",
                formatarEnergia(dispositivo.projetarConsumoMensalKWh()) + " kWh",
                moedaFormat.format(custoMensal)
            });
        }
    }

    private void iniciarMonitoramento() {
        if (sistema.obterDispositivos().isEmpty()) {
            mostrarErro("Cadastre um dispositivo antes de iniciar o monitoramento.");
            return;
        }

        timerMonitoramento.start();
        atualizarMonitoramentoTempoReal();
    }

    private void pararMonitoramento() {
        timerMonitoramento.stop();
        labelStatusMonitoramento.setText("Parado");
    }

    private void atualizarMonitoramentoSeParado() {
        if (timerMonitoramento != null && !timerMonitoramento.isRunning()) {
            atualizarMonitoramentoTempoReal();
        }
    }

    private OpcaoSimulacao obterOpcaoSimulacaoSelecionada() {
        if (comboEscalaSimulacao == null) {
            return new OpcaoSimulacao("1 segundo", 1);
        }

        Object selecionado = comboEscalaSimulacao.getSelectedItem();
        if (selecionado instanceof OpcaoSimulacao) {
            return (OpcaoSimulacao) selecionado;
        }

        return new OpcaoSimulacao("1 segundo", 1);
    }

    private double obterSegundosPorLeituraSelecionado() {
        return obterOpcaoSimulacaoSelecionada().getSegundos();
    }

    private void atualizarTextoEscalaSimulacao() {
        if (labelEscalaMonitoramento != null) {
            labelEscalaMonitoramento.setText("1 s real = "
                    + obterOpcaoSimulacaoSelecionada().getNome()
                    + " simulado");
        }
    }

    private void atualizarMonitoramentoTempoReal() {
        if (modeloMonitoramento == null) {
            return;
        }

        modeloMonitoramento.setRowCount(0);

        String horario = horarioFormat.format(new Date());
        double potenciaTotal = 0;
        boolean monitorando = timerMonitoramento != null && timerMonitoramento.isRunning();
        double duracaoLeitura = obterSegundosPorLeituraSelecionado();
        List<LeituraSimulada> leiturasSimuladas = new ArrayList<LeituraSimulada>();

        Calendar dataSimuladaAtual = historicoConsumo.obterDataAtual();
        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            double potenciaMedida = simuladorEnergia.simularPotenciaMedida(dispositivo, dataSimuladaAtual);
            double corrente = potenciaMedida / TENSAO_PADRAO;
            SensorEnergia sensor = new SensorEnergia(
                    "RT-" + dispositivo.getNome(),
                    dispositivo,
                    TENSAO_PADRAO,
                    corrente,
                    duracaoLeitura);
            String status = obterStatusMonitoramento(dispositivo, sensor.medirPotencia());

            potenciaTotal += sensor.medirPotencia();

            if (monitorando) {
                dispositivo.registrarLeituraSensor(sensor.medirPotencia(), sensor.getDuracaoLeituraSegundos());
                leiturasSimuladas.add(new LeituraSimulada(dispositivo.getNome(), sensor.medirPotencia()));
            }

            modeloMonitoramento.addRow(new Object[] {
                horario,
                formatarTempo(sensor.getDuracaoLeituraSegundos()),
                dispositivo.getNome(),
                formatarNumero(TENSAO_PADRAO),
                formatarNumero(corrente),
                formatarNumero(sensor.medirPotencia()),
                status
            });
        }

        double energiaLeituraKWh = (potenciaTotal * duracaoLeitura)
                / (ConstantesEnergia.SEGUNDOS_POR_HORA * ConstantesEnergia.FATOR_CONVERSAO_KWATTS);
        double custoLeitura = energiaLeituraKWh * sistema.obterTarifaEnergia();
        labelPotenciaAtual.setText("Potencia atual: " + formatarNumero(potenciaTotal) + " W");
        labelCustoHoraAtual.setText("Energia/leitura: " + formatarEnergia(energiaLeituraKWh)
                + " kWh | Custo: " + moedaFormat.format(custoLeitura));

        if (monitorando) {
            registrarPeriodoNoHistorico(leiturasSimuladas, duracaoLeitura);
            adicionarHistoricoPotencia(potenciaTotal);
            labelStatusMonitoramento.setText("Monitorando - data simulada "
                    + dataSimuladaFormat.format(historicoConsumo.obterDataAtual().getTime()));
            contadorLeiturasTempoReal++;
            if (contadorLeiturasTempoReal % 5 == 0) {
                registrarResumoTempoReal(horario, potenciaTotal, energiaLeituraKWh, custoLeitura);
            }
        } else {
            labelStatusMonitoramento.setText("Pre-visualizacao " + horario);
        }

        atualizarTabela();
        atualizarResumo();
        atualizarRelatorioMensal();
        atualizarCalendarioMensal();
        atualizarGraficos();
    }

    private String obterStatusMonitoramento(DispositivoEletrico dispositivo, double potenciaMedida) {
        if (potenciaMedida <= LIMITE_USO_WATTS) {
            return "Desligado";
        }
        if (potenciaMedida > LIMITE_ALERTA_WATTS) {
            return "Alerta: sobrecarga";
        }
        if (potenciaMedida > dispositivo.getPotenciaWatts() * 1.05) {
            return "Acima do esperado";
        }
        if (potenciaMedida < dispositivo.getPotenciaWatts() * 0.95) {
            return "Abaixo do esperado";
        }
        return "Normal";
    }

    private void registrarPeriodoNoHistorico(List<LeituraSimulada> leituras, double duracaoSegundos) {
        historicoConsumo.registrarPeriodo(leituras, duracaoSegundos);
        mesSelecionadoCalendario = historicoConsumo.obterMesAtual();
        selecionarAnoCalendario(historicoConsumo.obterAnoAtual());
    }

    private void registrarResumoTempoReal(
            String horario,
            double potenciaTotal,
            double energiaLeituraKWh,
            double custoLeitura) {
        areaLeituras.append("[Tempo real " + horario + "] "
                + "Potencia total: " + formatarNumero(potenciaTotal) + " W"
                + " | Escala: " + obterOpcaoSimulacaoSelecionada().getNome()
                + " | Energia: " + formatarEnergia(energiaLeituraKWh) + " kWh"
                + " | Custo: " + moedaFormat.format(custoLeitura)
                + System.lineSeparator());
        areaLeituras.setCaretPosition(areaLeituras.getDocument().getLength());
    }

    private void atualizarRelatorioMensal() {
        if (areaRelatorioMensal == null) {
            return;
        }

        areaRelatorioMensal.setText(gerarRelatorioMensal());
        areaRelatorioMensal.setCaretPosition(0);
    }

    private void adicionarHistoricoPotencia(double potenciaTotal) {
        historicoPotenciaTempoReal.add(potenciaTotal);
        if (historicoPotenciaTempoReal.size() > 60) {
            historicoPotenciaTempoReal.remove(0);
        }
    }

    private void limparHistoricoGraficoTempoReal() {
        historicoPotenciaTempoReal.clear();
        atualizarGraficos();
    }

    private void atualizarGraficos() {
        if (graficoConsumoMensal != null) {
            graficoConsumoMensal.repaint();
        }

        if (graficoConsumoDiario != null) {
            graficoConsumoDiario.repaint();
        }

        if (graficoPotenciaTempoReal != null) {
            graficoPotenciaTempoReal.repaint();
        }

        if (labelResumoGraficos != null) {
            labelResumoGraficos.setText("Dispositivos: " + sistema.obterQuantidadeDispositivos()
                    + " | Proj. mensal: " + formatarEnergia(sistema.calcularConsumoMensal()) + " kWh"
                    + " | Consumo do mes: " + formatarEnergia(calcularEnergiaMesGrafico()) + " kWh"
                    + " | Amostras: " + historicoPotenciaTempoReal.size());
        }
    }

    private void atualizarCalendarioMensal() {
        if (botoesMesesCalendario == null) {
            return;
        }

        double maiorEnergia = obterMaiorEnergiaMensal();
        double energiaAnual = obterEnergiaAnualRegistrada();

        for (int i = 0; i < MESES_ANO; i++) {
            JButton botao = botoesMesesCalendario[i];
            int diasMes = obterDiasDoMes(i);
            double energiaMes = calcularEnergiaMes(i);
            double custoMes = energiaMes * sistema.obterTarifaEnergia();

            botao.setText("<html><body style='text-align:center;color:#e8edf5'>"
                    + "<b>" + obterNomeMes(i) + "</b><br>"
                    + diasMes + " dias<br>"
                    + formatarEnergia(energiaMes) + " kWh<br>"
                    + moedaFormat.format(custoMes)
                    + "</body></html>");
            botao.setBackground(obterCorMesCalendario(energiaMes, maiorEnergia));
            botao.setForeground(COR_TEXTO);
            botao.setFocusPainted(false);
            botao.setContentAreaFilled(false);
            botao.setOpaque(true);
            botao.setBorder(BorderFactory.createLineBorder(
                    i == mesSelecionadoCalendario ? COR_PRIMARIA : COR_BORDA,
                    i == mesSelecionadoCalendario ? 3 : 1));
        }

        labelResumoCalendario.setText("Ano: " + obterAnoCalendario()
                + " | Inicio: " + dataSimuladaFormat.format(historicoConsumo.obterDataInicial().getTime())
                + " | Data simulada atual: " + dataSimuladaFormat.format(historicoConsumo.obterDataAtual().getTime())
                + " | Energia simulada no ano: " + formatarEnergia(energiaAnual) + " kWh");

        atualizarDetalhesMesCalendario();
    }

    private void atualizarDetalhesMesCalendario() {
        if (modeloDetalhesCalendario == null) {
            return;
        }

        int diasMes = obterDiasDoMes(mesSelecionadoCalendario);
        double energiaMes = calcularEnergiaMes(mesSelecionadoCalendario);
        double custoMes = energiaMes * sistema.obterTarifaEnergia();

        labelMesSelecionadoCalendario.setText(
                "Analise de " + obterNomeMes(mesSelecionadoCalendario)
                        + " de " + obterAnoCalendario()
                        + " (" + diasMes + " dias)");
        modeloDetalhesCalendario.setRowCount(0);

        String maiorDispositivo = "-";
        double maiorEnergiaDispositivo = 0;

        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            double energiaDispositivo = calcularEnergiaDispositivoMes(dispositivo.getNome(), mesSelecionadoCalendario);
            if (energiaDispositivo <= 0) {
                continue;
            }

            if (energiaDispositivo > maiorEnergiaDispositivo) {
                maiorEnergiaDispositivo = energiaDispositivo;
                maiorDispositivo = dispositivo.getNome();
            }

            double participacao = energiaMes > 0 ? (energiaDispositivo / energiaMes) * 100 : 0;
            modeloDetalhesCalendario.addRow(new Object[] {
                dispositivo.getNome(),
                formatarEnergia(energiaDispositivo) + " kWh",
                moedaFormat.format(energiaDispositivo * sistema.obterTarifaEnergia()),
                formatarNumero(participacao) + "%"
            });
        }

        double mediaMensal = obterMediaMensalProjetada();
        String comparacao = "sem media suficiente";
        if (mediaMensal > 0) {
            double diferenca = ((energiaMes - mediaMensal) / mediaMensal) * 100;
            if (diferenca >= 0) {
                comparacao = formatarNumero(diferenca) + "% acima da media mensal do ano";
            } else {
                comparacao = formatarNumero(Math.abs(diferenca)) + "% abaixo da media mensal do ano";
            }
        }

        areaAnaliseCalendario.setText(
                "Mes analisado: " + obterNomeMes(mesSelecionadoCalendario)
                        + " de " + obterAnoCalendario() + System.lineSeparator()
                        + "Quantidade de dias: " + diasMes + System.lineSeparator()
                        + "Energia simulada no mes: " + formatarEnergia(energiaMes) + " kWh" + System.lineSeparator()
                        + "Custo simulado no mes: " + moedaFormat.format(custoMes) + System.lineSeparator()
                        + "Tempo monitorado no mes: "
                        + formatarTempo(obterTempoMonitoradoMes(mesSelecionadoCalendario)) + System.lineSeparator()
                        + "Maior consumidor do mes: " + maiorDispositivo
                        + " (" + formatarEnergia(maiorEnergiaDispositivo) + " kWh)" + System.lineSeparator()
                        + "Comparacao com a media: " + comparacao + System.lineSeparator()
                        + "Energia simulada no ano: " + formatarEnergia(obterEnergiaAnualRegistrada())
                        + " kWh");
        areaAnaliseCalendario.setCaretPosition(0);
    }

    private void abrirAnaliseDiariaMes(int indiceMes) {
        int diasMes = obterDiasDoMes(indiceMes);
        double energiaMes = calcularEnergiaMes(indiceMes);
        double custoMes = energiaMes * sistema.obterTarifaEnergia();
        double mediaDiaria = obterMediaDiariaMes(indiceMes);

        DefaultTableModel modeloDiario = new DefaultTableModel(
                new String[] {
                    "Dia",
                    "Semana",
                    "Energia simulada",
                    "Custo simulado",
                    "Tempo monitorado",
                    "Maior consumidor",
                    "Status"
                },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int maiorDia = 1;
        int menorDia = 1;
        double maiorEnergiaDia = -1;
        double menorEnergiaDia = Double.MAX_VALUE;

        for (int dia = 1; dia <= diasMes; dia++) {
            RegistroConsumoDiario registro = historicoConsumo.obterRegistroDia(obterAnoCalendario(), indiceMes, dia);
            double energiaDia = registro == null ? 0 : registro.obterEnergiaKWh();
            double custoDia = energiaDia * sistema.obterTarifaEnergia();
            long tempoDia = registro == null ? 0 : registro.obterTempoMonitoradoSegundos();

            if (energiaDia > maiorEnergiaDia) {
                maiorEnergiaDia = energiaDia;
                maiorDia = dia;
            }
            if (energiaDia < menorEnergiaDia) {
                menorEnergiaDia = energiaDia;
                menorDia = dia;
            }

            modeloDiario.addRow(new Object[] {
                dia,
                obterDiaSemana(indiceMes, dia),
                formatarEnergia(energiaDia) + " kWh",
                moedaFormat.format(custoDia),
                formatarTempo(tempoDia),
                obterMaiorConsumidorDia(registro),
                classificarConsumoDiario(energiaDia, mediaDiaria)
            });
        }

        JTable tabelaDiaria = new JTable(modeloDiario);
        tabelaDiaria.setFillsViewportHeight(true);
        tabelaDiaria.setRowHeight(24);

        JTextArea resumo = new JTextArea();
        resumo.setEditable(false);
        resumo.setLineWrap(true);
        resumo.setWrapStyleWord(true);
        resumo.setRows(6);
        resumo.setText("Analise diaria de " + obterNomeMes(indiceMes)
                + " de " + obterAnoCalendario() + System.lineSeparator()
                + "Dias do mes: " + diasMes + System.lineSeparator()
                + "Energia simulada no mes: " + formatarEnergia(energiaMes) + " kWh" + System.lineSeparator()
                + "Custo simulado no mes: " + moedaFormat.format(custoMes) + System.lineSeparator()
                + "Media dos dias com leitura: " + formatarEnergia(mediaDiaria) + " kWh" + System.lineSeparator()
                + "Periodo: " + dataSimuladaFormat.format(historicoConsumo.obterDataInicial().getTime())
                + " -> " + dataSimuladaFormat.format(historicoConsumo.obterDataAtual().getTime()) + System.lineSeparator()
                + "Maior consumo: dia " + maiorDia + " (" + formatarEnergia(Math.max(0, maiorEnergiaDia)) + " kWh)"
                + " | Menor consumo: dia " + menorDia + " (" + formatarEnergia(menorEnergiaDia == Double.MAX_VALUE ? 0 : menorEnergiaDia) + " kWh)");

        JDialog dialog = new JDialog(
                janela,
                "Analise diaria - " + obterNomeMes(indiceMes) + " de " + obterAnoCalendario(),
                true);
        dialog.setLayout(new BorderLayout(8, 8));
        dialog.add(new JScrollPane(tabelaDiaria), BorderLayout.CENTER);
        dialog.add(new JScrollPane(resumo), BorderLayout.SOUTH);
        dialog.setSize(820, 520);
        dialog.setLocationRelativeTo(janela);
        dialog.setVisible(true);
    }

    private void zerarColetasMensais() {
        int resposta = JOptionPane.showConfirmDialog(
                janela,
                "Zerar calendario, coletas dos sensores e graficos de tempo real?",
                "Confirmar limpeza",
                JOptionPane.YES_NO_OPTION);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            dispositivo.zerarColetaSensor();
        }

        historicoPotenciaTempoReal.clear();
        historicoConsumo.zerar();
        contadorLeiturasTempoReal = 0;
        mesSelecionadoCalendario = historicoConsumo.obterMesAtual();
        if (comboAnoCalendario != null) {
            selecionarAnoCalendario(historicoConsumo.obterAnoAtual());
        }

        atualizarTela();
    }

    private Color obterCorMesCalendario(double energiaMes, double maiorEnergia) {
        if (energiaMes <= 0 || maiorEnergia <= 0) {
            return COR_PAINEL_ELEVADO;
        }

        double intensidade = Math.min(1.0, energiaMes / maiorEnergia);
        int vermelho = 34 + (int) Math.round(72 * intensidade);
        int verde = 47 + (int) Math.round(70 * intensidade);
        int azul = 62 + (int) Math.round(110 * intensidade);
        return new Color(vermelho, verde, azul);
    }

    private int obterAnoCalendario() {
        if (comboAnoCalendario == null || comboAnoCalendario.getSelectedItem() == null) {
            return Calendar.getInstance().get(Calendar.YEAR);
        }

        return ((Integer) comboAnoCalendario.getSelectedItem()).intValue();
    }

    private int obterMesGrafico() {
        if (comboMesGrafico == null || comboMesGrafico.getSelectedIndex() < 0) {
            return Calendar.getInstance().get(Calendar.MONTH);
        }

        return comboMesGrafico.getSelectedIndex();
    }

    private int obterAnoGrafico() {
        if (comboAnoGrafico == null || comboAnoGrafico.getSelectedItem() == null) {
            return Calendar.getInstance().get(Calendar.YEAR);
        }

        return ((Integer) comboAnoGrafico.getSelectedItem()).intValue();
    }

    private void selecionarAnoCalendario(int ano) {
        if (comboAnoCalendario == null) {
            return;
        }

        Integer valorAno = Integer.valueOf(ano);
        for (int i = 0; i < comboAnoCalendario.getItemCount(); i++) {
            if (valorAno.equals(comboAnoCalendario.getItemAt(i))) {
                comboAnoCalendario.setSelectedItem(valorAno);
                return;
            }
        }

        comboAnoCalendario.addItem(valorAno);
        comboAnoCalendario.setSelectedItem(valorAno);
    }

    private int obterDiasDoMes(int indiceMes) {
        return obterDiasDoMes(indiceMes, obterAnoCalendario());
    }

    private int obterDiasDoMes(int indiceMes, int ano) {
        Calendar calendario = Calendar.getInstance();
        calendario.clear();
        calendario.set(Calendar.YEAR, ano);
        calendario.set(Calendar.MONTH, indiceMes);
        calendario.set(Calendar.DAY_OF_MONTH, 1);
        return calendario.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private String obterNomeMes(int indiceMes) {
        String[] nomesMeses = {
            "Janeiro",
            "Fevereiro",
            "Marco",
            "Abril",
            "Maio",
            "Junho",
            "Julho",
            "Agosto",
            "Setembro",
            "Outubro",
            "Novembro",
            "Dezembro"
        };
        return nomesMeses[indiceMes];
    }

    private String obterDiaSemana(int indiceMes, int dia) {
        Calendar calendario = Calendar.getInstance(LOCALE_BR);
        calendario.clear();
        calendario.set(Calendar.YEAR, obterAnoCalendario());
        calendario.set(Calendar.MONTH, indiceMes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);

        SimpleDateFormat formatoSemana = new SimpleDateFormat("EEE", LOCALE_BR);
        return formatoSemana.format(calendario.getTime());
    }

    private double obterMediaDiariaMes(int indiceMes) {
        return historicoConsumo.calcularMediaDiariaMes(
                obterAnoCalendario(),
                indiceMes,
                obterDiasDoMes(indiceMes));
    }

    private String obterMaiorConsumidorDia(RegistroConsumoDiario registro) {
        if (registro == null) {
            return "-";
        }

        return registro.obterMaiorConsumidor();
    }

    private String classificarConsumoDiario(double energiaDia, double mediaDiaria) {
        if (energiaDia <= 0) {
            return "Sem leitura";
        }
        if (mediaDiaria <= 0) {
            return "Sem media";
        }
        if (energiaDia > mediaDiaria * 1.08) {
            return "Acima da media";
        }
        if (energiaDia < mediaDiaria * 0.92) {
            return "Abaixo da media";
        }
        return "Dentro da media";
    }

    private double calcularEnergiaMes(int indiceMes) {
        return historicoConsumo.calcularEnergiaMes(obterAnoCalendario(), indiceMes);
    }

    private double calcularEnergiaMesGrafico() {
        return historicoConsumo.calcularEnergiaMes(obterAnoGrafico(), obterMesGrafico());
    }

    private double calcularEnergiaDispositivoMes(String nomeDispositivo, int indiceMes) {
        return historicoConsumo.calcularEnergiaDispositivoMes(
                nomeDispositivo,
                obterAnoCalendario(),
                indiceMes);
    }

    private long obterTempoMonitoradoMes(int indiceMes) {
        return historicoConsumo.obterTempoMonitoradoMes(obterAnoCalendario(), indiceMes);
    }

    private double obterEnergiaAnualRegistrada() {
        return historicoConsumo.calcularEnergiaAno(obterAnoCalendario());
    }

    private double obterMaiorEnergiaMensal() {
        double maior = 0;
        for (int mes = 0; mes < MESES_ANO; mes++) {
            maior = Math.max(maior, calcularEnergiaMes(mes));
        }
        return maior;
    }

    private double obterEnergiaDia(int indiceMes, int dia) {
        return obterEnergiaDia(obterAnoCalendario(), indiceMes, dia);
    }

    private double obterEnergiaDia(int ano, int indiceMes, int dia) {
        RegistroConsumoDiario registro = historicoConsumo.obterRegistroDia(
                ano,
                indiceMes,
                dia);
        return registro == null ? 0 : registro.obterEnergiaKWh();
    }

    private double obterMaiorEnergiaDiariaMes(int indiceMes) {
        return obterMaiorEnergiaDiariaMes(obterAnoCalendario(), indiceMes);
    }

    private double obterMaiorEnergiaDiariaMes(int ano, int indiceMes) {
        double maior = 0;
        int diasMes = obterDiasDoMes(indiceMes, ano);
        for (int dia = 1; dia <= diasMes; dia++) {
            maior = Math.max(maior, obterEnergiaDia(ano, indiceMes, dia));
        }
        return maior;
    }

    private double obterMediaMensalProjetada() {
        if (MESES_ANO == 0) {
            return 0;
        }

        return historicoConsumo.calcularMediaMensalAno(obterAnoCalendario());
    }

    private long obterTempoMonitoradoTotalSegundos() {
        long maiorTempo = 0;

        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            maiorTempo = Math.max(maiorTempo, dispositivo.obterTempoMonitoradoSegundos());
        }

        return maiorTempo;
    }

    private String gerarRelatorioMensal() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATORIO MENSAL DE CONSUMO DE ENERGIA").append(System.lineSeparator());
        relatorio.append("Gerado em: ").append(dataHoraFormat.format(new Date())).append(System.lineSeparator());
        relatorio.append("Base: tempo e energia coletados pelos sensores").append(System.lineSeparator());
        relatorio.append("Ano analisado: ").append(obterAnoCalendario()).append(System.lineSeparator());
        relatorio.append("Escala atual da simulacao: 1 leitura = ")
                .append(obterOpcaoSimulacaoSelecionada().getNome())
                .append(System.lineSeparator());
        relatorio.append("Tarifa: ").append(moedaFormat.format(sistema.obterTarifaEnergia()))
                .append(" por kWh").append(System.lineSeparator());
        relatorio.append(System.lineSeparator());

        if (sistema.obterDispositivos().isEmpty()) {
            relatorio.append("Nenhum dispositivo cadastrado.").append(System.lineSeparator());
            return relatorio.toString();
        }

        DispositivoEletrico maiorConsumidor = null;
        double maiorConsumo = -1;

        relatorio.append("DISPOSITIVOS").append(System.lineSeparator());
        relatorio.append("------------------------------------------------------------").append(System.lineSeparator());

        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            double consumoMensal = dispositivo.projetarConsumoMensalKWh();
            double custoMensal = consumoMensal * sistema.obterTarifaEnergia();

            if (consumoMensal > maiorConsumo) {
                maiorConsumo = consumoMensal;
                maiorConsumidor = dispositivo;
            }

            relatorio.append(dispositivo.getNome()).append(System.lineSeparator());
            relatorio.append("  Potencia: ").append(formatarNumero(dispositivo.getPotenciaWatts())).append(" W")
                    .append(System.lineSeparator());
            relatorio.append("  Tempo monitorado: ").append(formatarTempo(dispositivo.obterTempoMonitoradoSegundos()))
                    .append(System.lineSeparator());
            relatorio.append("  Tempo em uso detectado: ").append(formatarTempo(dispositivo.obterTempoUsoSegundos()))
                    .append(System.lineSeparator());
            relatorio.append("  Energia coletada: ").append(formatarEnergia(dispositivo.obterEnergiaColetadaKWh()))
                    .append(" kWh").append(System.lineSeparator());
            relatorio.append("  Projecao mensal: ").append(formatarEnergia(consumoMensal))
                    .append(" kWh").append(System.lineSeparator());
            relatorio.append("  Custo mensal projetado: ").append(moedaFormat.format(custoMensal))
                    .append(System.lineSeparator());
            relatorio.append(System.lineSeparator());
        }

        relatorio.append("TOTAIS").append(System.lineSeparator());
        relatorio.append("------------------------------------------------------------").append(System.lineSeparator());
        relatorio.append("Quantidade de dispositivos: ").append(sistema.obterQuantidadeDispositivos())
                .append(System.lineSeparator());
        relatorio.append("Energia coletada total: ").append(formatarEnergia(sistema.calcularConsumoDiario()))
                .append(" kWh").append(System.lineSeparator());
        relatorio.append("Projecao mensal total: ").append(formatarEnergia(sistema.calcularConsumoMensal()))
                .append(" kWh").append(System.lineSeparator());
        relatorio.append("Custo mensal projetado: ").append(moedaFormat.format(sistema.calcularCustoMensal()))
                .append(System.lineSeparator());

        if (maiorConsumidor != null) {
            relatorio.append("Maior consumo: ").append(maiorConsumidor.getNome())
                    .append(" (").append(formatarEnergia(maiorConsumo)).append(" kWh/mes)")
                    .append(System.lineSeparator());
        }

        relatorio.append(System.lineSeparator());
        relatorio.append("ANALISE MES A MES").append(System.lineSeparator());
        relatorio.append("------------------------------------------------------------").append(System.lineSeparator());
        for (int i = 0; i < MESES_ANO; i++) {
            double energiaMes = calcularEnergiaMes(i);
            relatorio.append(obterNomeMes(i))
                    .append(" | Dias: ").append(obterDiasDoMes(i))
                    .append(" | Energia: ").append(formatarEnergia(energiaMes)).append(" kWh")
                    .append(" | Custo: ").append(moedaFormat.format(energiaMes * sistema.obterTarifaEnergia()))
                    .append(System.lineSeparator());
        }
        relatorio.append("Energia simulada no ano: ").append(formatarEnergia(obterEnergiaAnualRegistrada()))
                .append(" kWh").append(System.lineSeparator());
        relatorio.append("Custo simulado no ano: ")
                .append(moedaFormat.format(obterEnergiaAnualRegistrada() * sistema.obterTarifaEnergia()))
                .append(System.lineSeparator());

        relatorio.append(System.lineSeparator());
        relatorio.append("Observacao: os meses sao preenchidos em sequencia a partir da data atual do computador.")
                .append(System.lineSeparator());

        return relatorio.toString();
    }

    private void exportarRelatorioMensal() {
        atualizarRelatorioMensal();

        JFileChooser seletor = new JFileChooser();
        seletor.setSelectedFile(new File("relatorio-mensal-energia.txt"));

        int escolha = seletor.showSaveDialog(janela);
        if (escolha != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = seletor.getSelectedFile();
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(arquivo),
                StandardCharsets.UTF_8)) {
            writer.write(areaRelatorioMensal.getText());
            JOptionPane.showMessageDialog(janela, "Relatorio exportado com sucesso.");
        } catch (IOException excecao) {
            TratadorExcecoes.logErro("exportar relatorio mensal", "Falha de escrita", excecao);
            mostrarErro("Nao foi possivel exportar o relatorio.");
        }
    }

    private void atualizarResumo() {
        labelQuantidade.setText(criarTextoResumo(
                "Dispositivos",
                String.valueOf(sistema.obterQuantidadeDispositivos())));
        labelConsumoDiario.setText(criarTextoResumo(
                "Energia coletada",
                formatarEnergia(sistema.calcularConsumoDiario()) + " kWh"));
        labelConsumoMensal.setText(criarTextoResumo(
                "Proj. mensal",
                formatarEnergia(sistema.calcularConsumoMensal()) + " kWh"));
        labelCustoDiario.setText(criarTextoResumo(
                "Custo coletado",
                moedaFormat.format(sistema.calcularCustoDiario())));
        labelCustoMensal.setText(criarTextoResumo(
                "Custo proj.",
                moedaFormat.format(sistema.calcularCustoMensal())));
    }

    private double lerNumero(JTextField campo, String nomeCampo) {
        String texto = normalizarNumero(campo.getText());
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Informe o valor de " + nomeCampo + ".");
        }

        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException excecao) {
            throw new IllegalArgumentException("Valor invalido para " + nomeCampo + ".");
        }
    }

    private String normalizarNumero(String texto) {
        return ValidadorEntrada.normalizarNumero(texto);
    }

    private String formatarNumero(double valor) {
        return FormatadorEnergia.formatarNumero(valor);
    }

    private String formatarEnergia(double valor) {
        return FormatadorEnergia.formatarEnergia(valor);
    }

    private String formatarTempo(double segundos) {
        return formatarTempo(Math.round(segundos));
    }

    private String formatarTempo(long segundos) {
        return FormatadorEnergia.formatarTempo(segundos);
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(janela, mensagem, "Atencao", JOptionPane.WARNING_MESSAGE);
    }

    private static class OpcaoSimulacao {

        private final String nome;
        private final double segundos;

        OpcaoSimulacao(String nome, double segundos) {
            this.nome = nome;
            this.segundos = segundos;
        }

        String getNome() {
            return nome;
        }

        double getSegundos() {
            return segundos;
        }

        @Override
        public String toString() {
            return nome;
        }
    }

    private class GraficoConsumoMensalPanel extends JPanel {

        private final Color corBarra = new Color(83, 151, 225);
        private final Color corBarraDestaque = new Color(244, 169, 85);

        public GraficoConsumoMensalPanel() {
            setPreferredSize(new Dimension(640, 260));
            setBackground(COR_PAINEL);
            setBorder(criarBordaTituloEscura("Projecao mensal por dispositivo"));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();
            int margemEsquerda = 70;
            int margemDireita = 24;
            int margemTopo = 44;
            int margemBaixo = 72;
            int areaLargura = largura - margemEsquerda - margemDireita;
            int areaAltura = altura - margemTopo - margemBaixo;

            if (sistema.obterDispositivos().isEmpty()) {
                desenharMensagem(g2, "Cadastre dispositivos para visualizar o grafico mensal.", largura, altura);
                g2.dispose();
                return;
            }

            double maiorConsumo = obterMaiorConsumoMensal();
            if (maiorConsumo <= 0) {
                maiorConsumo = 1;
            }

            desenharEixos(g2, margemEsquerda, margemTopo, areaLargura, areaAltura);

            int quantidade = sistema.obterQuantidadeDispositivos();
            int larguraSlot = Math.max(1, areaLargura / quantidade);
            int larguraBarra = Math.max(24, Math.min(72, larguraSlot - 18));
            FontMetrics metricas = g2.getFontMetrics();

            for (int i = 0; i < quantidade; i++) {
                DispositivoEletrico dispositivo = sistema.obterDispositivos().get(i);
                double consumo = dispositivo.projetarConsumoMensalKWh();
                double custo = consumo * sistema.obterTarifaEnergia();
                int alturaBarra = (int) Math.round((consumo / maiorConsumo) * (areaAltura - 10));
                int x = margemEsquerda + (i * larguraSlot) + ((larguraSlot - larguraBarra) / 2);
                int y = margemTopo + areaAltura - alturaBarra;

                g2.setColor(consumo == maiorConsumo ? corBarraDestaque : corBarra);
                g2.fillRoundRect(x, y, larguraBarra, alturaBarra, 8, 8);

                g2.setColor(COR_TEXTO);
                String valor = formatarEnergia(consumo) + " kWh";
                g2.drawString(valor, centralizarTexto(metricas, valor, x, larguraBarra), y - 6);

                String nome = abreviarTexto(dispositivo.getNome(), 16);
                g2.drawString(nome, centralizarTexto(metricas, nome, x, larguraBarra), margemTopo + areaAltura + 20);

                String custoTexto = moedaFormat.format(custo);
                g2.drawString(custoTexto, centralizarTexto(metricas, custoTexto, x, larguraBarra), margemTopo + areaAltura + 38);
            }

            g2.setColor(COR_TEXTO_SUAVE);
            g2.drawString("kWh/mes", 12, margemTopo + 10);
            g2.drawString(formatarEnergia(maiorConsumo), 12, margemTopo + 24);
            g2.dispose();
        }
    }

    private class GraficoPotenciaTempoRealPanel extends JPanel {

        public GraficoPotenciaTempoRealPanel() {
            setPreferredSize(new Dimension(640, 220));
            setBackground(COR_PAINEL);
            setBorder(criarBordaTituloEscura("Potencia total em tempo real"));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();
            int margemEsquerda = 70;
            int margemDireita = 24;
            int margemTopo = 42;
            int margemBaixo = 48;
            int areaLargura = largura - margemEsquerda - margemDireita;
            int areaAltura = altura - margemTopo - margemBaixo;

            if (historicoPotenciaTempoReal.isEmpty()) {
                desenharMensagem(g2, "Inicie o monitoramento em tempo real para formar a curva de potencia.", largura, altura);
                g2.dispose();
                return;
            }

            double maiorPotencia = obterMaiorValor(historicoPotenciaTempoReal);
            if (maiorPotencia <= 0) {
                maiorPotencia = 1;
            }

            desenharEixos(g2, margemEsquerda, margemTopo, areaLargura, areaAltura);

            g2.setColor(new Color(76, 196, 140));
            g2.setStroke(new BasicStroke(3f));

            int quantidade = historicoPotenciaTempoReal.size();
            int xAnterior = margemEsquerda;
            int yAnterior = calcularY(historicoPotenciaTempoReal.get(0), maiorPotencia, margemTopo, areaAltura);

            for (int i = 1; i < quantidade; i++) {
                int xAtual = margemEsquerda + (int) Math.round((i / (double) Math.max(1, quantidade - 1)) * areaLargura);
                int yAtual = calcularY(historicoPotenciaTempoReal.get(i), maiorPotencia, margemTopo, areaAltura);

                g2.drawLine(xAnterior, yAnterior, xAtual, yAtual);
                xAnterior = xAtual;
                yAnterior = yAtual;
            }

            g2.setColor(new Color(76, 196, 140));
            for (int i = 0; i < quantidade; i++) {
                int x = margemEsquerda + (int) Math.round((i / (double) Math.max(1, quantidade - 1)) * areaLargura);
                int y = calcularY(historicoPotenciaTempoReal.get(i), maiorPotencia, margemTopo, areaAltura);
                g2.fillOval(x - 3, y - 3, 6, 6);
            }

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(COR_TEXTO_SUAVE);
            g2.drawString("W", 26, margemTopo + 10);
            g2.drawString(formatarNumero(maiorPotencia), 12, margemTopo + 24);
            g2.drawString("Ultimos " + quantidade + " pontos", margemEsquerda, altura - 16);
            g2.dispose();
        }

        private int calcularY(double valor, double maiorValor, int margemTopo, int areaAltura) {
            return margemTopo + areaAltura - (int) Math.round((valor / maiorValor) * (areaAltura - 10));
        }
    }

    private class GraficoConsumoDiarioPanel extends JPanel {

        private final Color corLinha = new Color(76, 196, 140);
        private final Color corPonto = new Color(132, 232, 178);
        private int diaDestacado = -1;

        public GraficoConsumoDiarioPanel() {
            setPreferredSize(new Dimension(640, 230));
            setBackground(COR_PAINEL);
            setBorder(criarBordaTituloEscura("Consumo diario do mes selecionado"));
            setToolTipText("");

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent evento) {
                    atualizarDiaDestacado(evento.getX(), evento.getY());
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent evento) {
                    diaDestacado = -1;
                    setToolTipText("");
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();
            int margemEsquerda = 82;
            int margemDireita = 28;
            int margemTopo = 44;
            int margemBaixo = 58;
            int areaLargura = largura - margemEsquerda - margemDireita;
            int areaAltura = altura - margemTopo - margemBaixo;

            int mesGrafico = obterMesGrafico();
            int anoGrafico = obterAnoGrafico();
            int diasMes = obterDiasDoMes(mesGrafico, anoGrafico);
            double maiorEnergiaDia = obterMaiorEnergiaDiariaMes(anoGrafico, mesGrafico);
            if (maiorEnergiaDia <= 0) {
                desenharMensagem(g2, "Inicie o monitoramento para formar o consumo diario do mes.", largura, altura);
                g2.dispose();
                return;
            }

            desenharEixos(g2, margemEsquerda, margemTopo, areaLargura, areaAltura);

            int[] pontosX = new int[diasMes];
            int[] pontosY = new int[diasMes];
            for (int dia = 1; dia <= diasMes; dia++) {
                double energiaDia = obterEnergiaDia(anoGrafico, mesGrafico, dia);
                int indice = dia - 1;
                pontosX[indice] = margemEsquerda
                        + (int) Math.round(((dia - 1) / (double) Math.max(1, diasMes - 1)) * areaLargura);
                pontosY[indice] = calcularY(energiaDia, maiorEnergiaDia, margemTopo, areaAltura);
            }

            g2.setStroke(new BasicStroke(3f));
            g2.setColor(corLinha);
            for (int indice = 1; indice < diasMes; indice++) {
                g2.drawLine(pontosX[indice - 1], pontosY[indice - 1], pontosX[indice], pontosY[indice]);
            }

            FontMetrics metricas = g2.getFontMetrics();
            int passoRotulo = Math.max(1, diasMes / 8);
            for (int dia = 1; dia <= diasMes; dia++) {
                int indice = dia - 1;
                double energiaDia = obterEnergiaDia(anoGrafico, mesGrafico, dia);
                g2.setColor(corPonto);
                g2.fillOval(pontosX[indice] - 4, pontosY[indice] - 4, 8, 8);

                if (dia == 1 || dia == diasMes || dia % passoRotulo == 0) {
                    g2.setColor(COR_TEXTO_SUAVE);
                    String diaTexto = String.valueOf(dia);
                    g2.drawString(diaTexto, centralizarTexto(metricas, diaTexto, pontosX[indice] - 14, 28),
                            margemTopo + areaAltura + 20);
                }

                if (energiaDia > 0 && energiaDia == maiorEnergiaDia) {
                    String energiaTexto = formatarEnergia(energiaDia) + " kWh";
                    g2.drawString(energiaTexto, centralizarTexto(metricas, energiaTexto, pontosX[indice] - 44, 88),
                            Math.max(margemTopo + 12, pontosY[indice] - 8));
                }
            }

            desenharDestaqueInterativo(g2, pontosX, pontosY, maiorEnergiaDia, margemTopo, areaAltura);

            g2.setColor(COR_TEXTO_SUAVE);
            g2.drawString(obterNomeMes(mesGrafico) + " de " + anoGrafico,
                    margemEsquerda, altura - 16);
            g2.drawString("Max: " + formatarEnergia(maiorEnergiaDia) + " kWh", 12, margemTopo + 16);
            g2.drawString("Total: " + formatarEnergia(calcularEnergiaMesGrafico()) + " kWh",
                    12, margemTopo + 32);
            g2.dispose();
        }

        private int calcularY(double valor, double maiorValor, int margemTopo, int areaAltura) {
            return margemTopo + areaAltura - (int) Math.round((valor / maiorValor) * (areaAltura - 10));
        }

        private void atualizarDiaDestacado(int mouseX, int mouseY) {
            int largura = getWidth();
            int altura = getHeight();
            int margemEsquerda = 82;
            int margemDireita = 28;
            int margemTopo = 44;
            int margemBaixo = 58;
            int areaLargura = largura - margemEsquerda - margemDireita;
            int areaAltura = altura - margemTopo - margemBaixo;

            if (mouseX < margemEsquerda || mouseX > margemEsquerda + areaLargura
                    || mouseY < margemTopo - 16 || mouseY > margemTopo + areaAltura + 28) {
                if (diaDestacado != -1) {
                    diaDestacado = -1;
                    setToolTipText("");
                    repaint();
                }
                return;
            }

            int diasMes = obterDiasDoMes(obterMesGrafico(), obterAnoGrafico());
            int novoDia = 1 + (int) Math.round(((mouseX - margemEsquerda) / (double) Math.max(1, areaLargura))
                    * Math.max(1, diasMes - 1));
            novoDia = Math.max(1, Math.min(diasMes, novoDia));

            if (novoDia != diaDestacado) {
                diaDestacado = novoDia;
                double energiaDia = obterEnergiaDia(obterAnoGrafico(), obterMesGrafico(), diaDestacado);
                double custoDia = energiaDia * sistema.obterTarifaEnergia();
                setToolTipText("Dia " + diaDestacado
                        + " | " + formatarEnergia(energiaDia) + " kWh"
                        + " | " + moedaFormat.format(custoDia));
                repaint();
            }
        }

        private void desenharDestaqueInterativo(
                Graphics2D g2,
                int[] pontosX,
                int[] pontosY,
                double maiorEnergiaDia,
                int margemTopo,
                int areaAltura) {
            if (diaDestacado < 1 || diaDestacado > pontosX.length) {
                return;
            }

            int indice = diaDestacado - 1;
            int x = pontosX[indice];
            int y = pontosY[indice];
            double energiaDia = obterEnergiaDia(obterAnoGrafico(), obterMesGrafico(), diaDestacado);
            double custoDia = energiaDia * sistema.obterTarifaEnergia();

            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(122, 216, 161, 120));
            g2.drawLine(x, margemTopo, x, margemTopo + areaAltura);

            g2.setColor(new Color(255, 208, 133));
            g2.fillOval(x - 7, y - 7, 14, 14);
            g2.setColor(COR_FUNDO);
            g2.fillOval(x - 3, y - 3, 6, 6);

            String linha1 = "Dia " + diaDestacado;
            String linha2 = formatarEnergia(energiaDia) + " kWh";
            String linha3 = moedaFormat.format(custoDia);
            FontMetrics metricas = g2.getFontMetrics();
            int caixaLargura = Math.max(metricas.stringWidth(linha2), metricas.stringWidth(linha3)) + 24;
            caixaLargura = Math.max(caixaLargura, metricas.stringWidth(linha1) + 24);
            int caixaAltura = 58;
            int caixaX = Math.min(getWidth() - caixaLargura - 12, x + 12);
            int caixaY = Math.max(14, y - caixaAltura - 12);

            g2.setColor(new Color(18, 22, 29, 235));
            g2.fillRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 8, 8);
            g2.setColor(COR_PRIMARIA);
            g2.drawRoundRect(caixaX, caixaY, caixaLargura, caixaAltura, 8, 8);

            g2.setColor(COR_TEXTO);
            g2.drawString(linha1, caixaX + 12, caixaY + 17);
            g2.setColor(COR_TEXTO_SUAVE);
            g2.drawString(linha2, caixaX + 12, caixaY + 35);
            g2.drawString(linha3, caixaX + 12, caixaY + 51);
        }
    }

    private void desenharEixos(Graphics2D g2, int x, int y, int largura, int altura) {
        g2.setColor(COR_GRADE);
        for (int i = 0; i <= 4; i++) {
            int linhaY = y + (i * altura / 4);
            g2.drawLine(x, linhaY, x + largura, linhaY);
        }

        g2.setColor(COR_BORDA);
        g2.drawLine(x, y, x, y + altura);
        g2.drawLine(x, y + altura, x + largura, y + altura);
    }

    private void desenharMensagem(Graphics2D g2, String mensagem, int largura, int altura) {
        FontMetrics metricas = g2.getFontMetrics();
        int x = (largura - metricas.stringWidth(mensagem)) / 2;
        int y = altura / 2;
        g2.setColor(COR_TEXTO_SUAVE);
        g2.drawString(mensagem, Math.max(16, x), y);
    }

    private int centralizarTexto(FontMetrics metricas, String texto, int x, int largura) {
        return x + ((largura - metricas.stringWidth(texto)) / 2);
    }

    private double obterMaiorConsumoMensal() {
        double maiorConsumo = 0;
        for (DispositivoEletrico dispositivo : sistema.obterDispositivos()) {
            maiorConsumo = Math.max(maiorConsumo, dispositivo.projetarConsumoMensalKWh());
        }
        return maiorConsumo;
    }

    private double obterMaiorValor(List<Double> valores) {
        double maiorValor = 0;
        for (Double valor : valores) {
            maiorValor = Math.max(maiorValor, valor.doubleValue());
        }
        return maiorValor;
    }

    private String abreviarTexto(String texto, int limite) {
        if (texto.length() <= limite) {
            return texto;
        }
        return texto.substring(0, limite - 3) + "...";
    }
}
