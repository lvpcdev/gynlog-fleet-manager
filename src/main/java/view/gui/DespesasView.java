package view.gui;

import controller.MovimentacaoController;
import controller.TipoDespesaController;
import controller.VeiculoController;
import exceptions.ValidacaoException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import model.enums.StatusTipoDespesa;
import util.CsvExporter;
import util.Fila;
import view.util.CaixaAltaComLimiteFilter;
import view.util.DatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DespesasView extends JFrame {

    private final MovimentacaoController movimentacaoController;
    private final TipoDespesaController tipoDespesaController;
    private final VeiculoController veiculoController;

    private JComboBox<Veiculo> comboBoxVeiculos;
    private JComboBox<TipoDespesa> comboBoxTiposDespesa;
    private JTextField campoValor;
    private DatePicker campoData;
    private JTextField campoDescricao;
    private DefaultTableModel tableModelMovimentacoes;
    private DefaultTableModel tableModelPendentes;
    private Fila<Movimentacao> filaPendentes;

    public DespesasView() {
        this.movimentacaoController = new MovimentacaoController();
        this.tipoDespesaController = new TipoDespesaController();
        this.veiculoController = new VeiculoController();

        setTitle("GESTÃO DE DESPESAS");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setContentPane(buildMainPanel(true));
        setVisible(true);
    }

    DespesasView(boolean forEmbed) {
        this.movimentacaoController = new MovimentacaoController();
        this.tipoDespesaController = new TipoDespesaController();
        this.veiculoController = new VeiculoController();
    }

    public static JPanel createMainPanel() {
        DespesasView d = new DespesasView(false);
        return d.buildMainPanel(false);
    }

    public JPanel getMainPanel() {
        return buildMainPanel(false);
    }

    public void refreshData() {
        atualizarDados();
    }

    private void aplicarFiltroValor(JTextField campo) {
        AbstractDocument doc = (AbstractDocument) campo.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String textoAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String textoResultante = textoAtual.substring(0, offset) + string + textoAtual.substring(offset);
                if (textoValido(textoResultante)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String textoAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String textoResultante = textoAtual.substring(0, offset) + text + textoAtual.substring(offset + length);
                if (textoValido(textoResultante)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean textoValido(String texto) {
                if (!texto.matches("[0-9]*[.,]?[0-9]{0,2}")) {
                    return false;
                }
                String normalizado = texto.replace(",", ".");
                if (normalizado.isEmpty()) return true;
                try {
                    BigDecimal valor = new BigDecimal(normalizado);
                    return valor.compareTo(new BigDecimal("99999999.99")) <= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }

    private BigDecimal extrairValor(String texto) {
        String normalizado = texto.replace(",", ".").trim();
        if (normalizado.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(normalizado);
    }

    private JPanel buildMainPanel(boolean includeTopBackButton) {
        JPanel root = new JPanel(new BorderLayout());

        if (includeTopBackButton) {
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton botaoVoltar = new JButton("Voltar");
            topo.add(botaoVoltar);
            root.add(topo, BorderLayout.NORTH);

            botaoVoltar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new MenuView();
                            DespesasView.this.dispose();
                        }
                    });
                }
            });
        }

        JTabbedPane abas = new JTabbedPane();
        root.add(abas, BorderLayout.CENTER);

        abas.addTab("Registrar Despesa", criarPainelCadastroMovimentacao());
        abas.addTab("Histórico de Despesas", criarPainelListagem());
        abas.addTab("Fila de Aprovação", criarPainelFilaAprovacao());
        abas.addTab("Tipos de Despesa", criarPainelCadastroTipoDespesa());

        atualizarDados();

        abas.addChangeListener(e -> {
            if (abas.getSelectedIndex() == 0) {
                limparCamposCadastro();
            }
        });
        return root;
    }

    private JPanel criarPainelCadastroMovimentacao() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        comboBoxVeiculos = new JComboBox<Veiculo>();
        comboBoxVeiculos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Veiculo) {
                    Veiculo veiculo = (Veiculo) value;
                    setText(veiculo.getPlaca() + " (" + veiculo.getModelo() + ")");
                } else if (value == null) {
                    setText("Selecione um veículo");
                }
                return this;
            }
        });

        comboBoxTiposDespesa = new JComboBox<TipoDespesa>();
        comboBoxTiposDespesa.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TipoDespesa) {
                    setText(((TipoDespesa) value).getDescricao());
                } else if (value == null) {
                    setText("Selecione uma despesa");
                }
                return this;
            }
        });
        campoValor = new JTextField(10);
        campoData = new DatePicker(LocalDate.now());
        campoDescricao = new JTextField(25);

        JTextField campoQuilometragem = new JTextField(10);
        AbstractDocument docKm = (AbstractDocument) campoQuilometragem.getDocument();
        docKm.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        JLabel labelQuilometragem = new JLabel("Nova KM:");
        JLabel labelKmAnterior = new JLabel("KM anterior:");
        JLabel valorKmAnterior = new JLabel("—");

        campoQuilometragem.setVisible(false);
        labelQuilometragem.setVisible(false);
        labelKmAnterior.setVisible(false);
        valorKmAnterior.setVisible(false);

        JButton botaoSalvar = new JButton("Salvar Despesa");

        aplicarFiltroValor(campoValor);

        Runnable atualizarKmAnterior = () -> {
            Veiculo veiculoSelecionado = (Veiculo) comboBoxVeiculos.getSelectedItem();
            TipoDespesa tipoSelecionado = (TipoDespesa) comboBoxTiposDespesa.getSelectedItem();
            boolean ehCombustivel = tipoSelecionado != null && (
                    tipoSelecionado.getDescricao().toUpperCase().contains("COMBUSTIVEL") ||
                            tipoSelecionado.getDescricao().toUpperCase().contains("COMBUSTÍVEL"));

            campoQuilometragem.setVisible(ehCombustivel);
            labelQuilometragem.setVisible(ehCombustivel);
            labelKmAnterior.setVisible(ehCombustivel);
            valorKmAnterior.setVisible(ehCombustivel);

            if (ehCombustivel && veiculoSelecionado != null) {
                try {
                    boolean temRegistro = movimentacaoController.existeCombustivelMovimentacao(veiculoSelecionado.getId());
                    if (temRegistro) {
                        double kmAnterior = movimentacaoController.buscarUltimaQuilometragemVeiculo(veiculoSelecionado.getId());
                        valorKmAnterior.setText(String.format("%.0f km", kmAnterior));
                    } else {
                        valorKmAnterior.setText("Nenhum registro anterior");
                    }
                } catch (Exception ex) {
                    valorKmAnterior.setText("Erro ao buscar");
                }
            } else {
                valorKmAnterior.setText("—"); // ← reseta quando veículo for null
            }

            painel.revalidate();
            painel.repaint();
        };

        comboBoxTiposDespesa.addActionListener(e -> atualizarKmAnterior.run());
        comboBoxVeiculos.addActionListener(e -> atualizarKmAnterior.run());

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Veículo:"), gbc);
        gbc.gridy = 1;                 painel.add(new JLabel("Tipo de Despesa:"), gbc);
        gbc.gridy = 2;                 painel.add(new JLabel("Data:"), gbc);
        gbc.gridy = 3;                 painel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridy = 4;                 painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridy = 5;                 painel.add(labelQuilometragem, gbc);
        gbc.gridy = 6;                 painel.add(labelKmAnterior, gbc);


        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0; painel.add(comboBoxVeiculos, gbc);
        gbc.gridy = 1; painel.add(comboBoxTiposDespesa, gbc);
        gbc.gridy = 2; painel.add(campoData, gbc);
        gbc.gridy = 3; painel.add(campoValor, gbc);
        gbc.gridy = 4; painel.add(campoDescricao, gbc);
        gbc.gridy = 5; painel.add(campoQuilometragem, gbc);
        gbc.gridy = 6; painel.add(valorKmAnterior, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(botaoSalvar, gbc);

        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Veiculo veiculo = (Veiculo) comboBoxVeiculos.getSelectedItem();
                    TipoDespesa tipo = (TipoDespesa) comboBoxTiposDespesa.getSelectedItem();
                    if (veiculo == null) {
                        JOptionPane.showMessageDialog(DespesasView.this,
                                "Por favor, selecione um veículo.",
                                "Filtro Necessário",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (tipo == null) {
                        JOptionPane.showMessageDialog(DespesasView.this,
                                "Por favor, selecione um tipo de despesa.",
                                "Filtro Necessário",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    LocalDate data = campoData.getSelectedDate();
                    BigDecimal valor = extrairValor(campoValor.getText());
                    String descricao = campoDescricao.getText();

                    Double quilometragem = null;
                    if (campoQuilometragem.isVisible() && !campoQuilometragem.getText().trim().isEmpty()) {
                        quilometragem = Double.parseDouble(campoQuilometragem.getText().trim().replace(",", "."));

                        // ← validação km nova > km anterior
                        if (veiculo != null && movimentacaoController.existeCombustivelMovimentacao(veiculo.getId())) {
                            double kmAnterior = movimentacaoController.buscarUltimaQuilometragemVeiculo(veiculo.getId());
                            if (quilometragem <= kmAnterior) {
                                JOptionPane.showMessageDialog(DespesasView.this,
                                        "A quilometragem informada (" + quilometragem.intValue() + " km) deve ser\n" +
                                                "maior que a última registrada (" + (int) kmAnterior + " km).",
                                        "Quilometragem Inválida",
                                        JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }

                    Movimentacao novaMovimentacao = new Movimentacao(veiculo, tipo, descricao, data, valor);
                    novaMovimentacao.setQuilometragemAtual(quilometragem);

                    movimentacaoController.salvar(novaMovimentacao);
                    JOptionPane.showMessageDialog(DespesasView.this, "Despesa registrada com sucesso! Status: PENDENTE");
                    limparCamposCadastro();
                    campoQuilometragem.setText("");
                    atualizarDados();
                    atualizarTabelaPendentes();

                } catch (ValidacaoException ex) {
                    JOptionPane.showMessageDialog(DespesasView.this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Erro ao registrar despesa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        return painel;
    }

    private JPanel criarPainelListagem() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID", "Data", "Veículo", "Tipo", "Descrição", "Valor"};
        tableModelMovimentacoes = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(tableModelMovimentacoes);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botaoEditar = new JButton("Editar Despesa Selecionada");
        JButton botaoExcluir = new JButton("Excluir Despesa Selecionada");
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoExcluir);
        painel.add(painelBotoes, BorderLayout.SOUTH);
        JButton botaoExportarMovimentacoes = new JButton("Exportar CSV");
        botaoExportarMovimentacoes.setBackground(new Color(40, 167, 69));
        botaoExportarMovimentacoes.setForeground(Color.WHITE);
        botaoExportarMovimentacoes.setOpaque(true);
        botaoExportarMovimentacoes.setBorderPainted(false);
        botaoExportarMovimentacoes.setFocusPainted(false);
        painelBotoes.add(botaoExportarMovimentacoes);

        botaoEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabela.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Por favor, selecione uma despesa para editar.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    Long idMovimentacao = Long.parseLong(tableModelMovimentacoes.getValueAt(selectedRow, 0).toString());
                    Movimentacao movimentacaoParaEditar = movimentacaoController.buscarPorId(idMovimentacao);
                    JDialog janelaEdicao = new JDialog(DespesasView.this, "Editar Despesa", true);
                    janelaEdicao.setContentPane(criarPainelEdicao(movimentacaoParaEditar, janelaEdicao));
                    janelaEdicao.pack();
                    janelaEdicao.setLocationRelativeTo(DespesasView.this);
                    janelaEdicao.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botaoExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabela.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Por favor, selecione uma despesa para excluir.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Long id = Long.parseLong(tableModelMovimentacoes.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(DespesasView.this, "Tem certeza que deseja excluir a despesa ID " + id + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        movimentacaoController.excluir(id);
                        atualizarDados();
                        JOptionPane.showMessageDialog(DespesasView.this, "Despesa excluída com sucesso!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DespesasView.this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        botaoExportarMovimentacoes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salvar CSV - Movimentações");
                fileChooser.setSelectedFile(new File("movimentacoes.csv"));
                if (fileChooser.showSaveDialog(DespesasView.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File arquivo = fileChooser.getSelectedFile();
                        String caminho = arquivo.getAbsolutePath().endsWith(".csv")
                                ? arquivo.getAbsolutePath()
                                : arquivo.getAbsolutePath() + ".csv";
                        CsvExporter.exportarMovimentacoes(movimentacaoController.listarAprovadas(), caminho);
                        JOptionPane.showMessageDialog(DespesasView.this, "Exportado com sucesso!\n" + caminho);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DespesasView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        return painel;
    }

    private JPanel criarPainelFilaAprovacao() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID", "Data", "Veículo", "Tipo", "Descrição", "Valor", "Status"};
        tableModelPendentes = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabelaPendentes = new JTable(tableModelPendentes);
        tabelaPendentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painel.add(new JScrollPane(tabelaPendentes), BorderLayout.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabelaPendentes.getColumnCount(); i++) {
            tabelaPendentes.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JLabel labelInfo = new JLabel("Movimentações pendentes são aprovadas ou reprovadas respeitando a ordem de cadastro.", JLabel.CENTER);
        painel.add(labelInfo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botaoAprovarProxima = new JButton("Aprovar Próxima da Fila");
        JButton botaoRejeitarProxima = new JButton("Rejeitar Próxima da Fila");
        JButton botaoRecarregar = new JButton("Recarregar Fila");

        painelBotoes.add(botaoAprovarProxima);
        painelBotoes.add(botaoRejeitarProxima);
        painelBotoes.add(botaoRecarregar);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        botaoRecarregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarTabelaPendentes();
            }
        });

        botaoAprovarProxima.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filaPendentes == null || filaPendentes.estaVazia()) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Não há movimentações pendentes na fila!", "Fila Vazia", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                try {
                    Movimentacao aprovada = movimentacaoController.aprovarProxima(filaPendentes);
                    JOptionPane.showMessageDialog(DespesasView.this,
                            "Movimentação aprovada!\n" +
                                    "ID: " + aprovada.getId() + "\n" +
                                    "Veículo: " + aprovada.getVeiculo().getPlaca() + "\n" +
                                    "Valor: " + aprovada.getValor(),
                            "Aprovada com sucesso!", JOptionPane.INFORMATION_MESSAGE);
                    atualizarTabelaPendentes();
                    atualizarDados();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Erro ao aprovar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botaoRejeitarProxima.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filaPendentes == null || filaPendentes.estaVazia()) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Não há movimentações pendentes na fila!", "Fila Vazia", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                try {
                    Movimentacao rejeitada = filaPendentes.removerInicio();
                    movimentacaoController.excluir(rejeitada.getId());
                    JOptionPane.showMessageDialog(DespesasView.this,
                            "Movimentação rejeitada e removida!\n" +
                                    "ID: " + rejeitada.getId() + "\n" +
                                    "Veículo: " + rejeitada.getVeiculo().getPlaca() + "\n" +
                                    "Valor: " + rejeitada.getValor(),
                            "Rejeitada!", JOptionPane.WARNING_MESSAGE);
                    atualizarTabelaPendentes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Erro ao rejeitar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        atualizarTabelaPendentes();

        return painel;
    }

    private void atualizarTabelaPendentes() {
        filaPendentes = movimentacaoController.listarPendentes();
        tableModelPendentes.setRowCount(0);

        Fila<Movimentacao> filaTemp = movimentacaoController.listarPendentes();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        while (!filaTemp.estaVazia()) {
            Movimentacao mov = filaTemp.removerInicio();
            tableModelPendentes.addRow(new Object[]{
                    mov.getId(),
                    mov.getData().format(formatter),
                    mov.getVeiculo().getPlaca(),
                    mov.getTipoDespesa().getDescricao(),
                    mov.getDescricao(),
                    currencyFormatter.format(mov.getValor()),
                    mov.getStatusMovimentacao()
            });
        }
    }

    private JPanel criarPainelCadastroTipoDespesa() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<TipoDespesa> listModel = new DefaultListModel<TipoDespesa>();
        JList<TipoDespesa> listaTiposDespesa = new JList<TipoDespesa>(listModel);
        listaTiposDespesa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelPrincipal.add(new JScrollPane(listaTiposDespesa), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        JButton botaoEditar = new JButton("Editar");
        botaoEditar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelBotoes.add(botaoEditar);
        painelPrincipal.add(painelBotoes, BorderLayout.EAST);

        JPanel painelAdicionar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField campoNovoTipo = new JTextField(20);
        AbstractDocument docNovoTipo = (AbstractDocument) campoNovoTipo.getDocument();
        docNovoTipo.setDocumentFilter(new CaixaAltaComLimiteFilter(25));
        JButton botaoSalvarNovo = new JButton("Salvar Novo Tipo");
        painelAdicionar.add(new JLabel("Novo Tipo:"));
        painelAdicionar.add(campoNovoTipo);
        painelAdicionar.add(botaoSalvarNovo);
        painelPrincipal.add(painelAdicionar, BorderLayout.SOUTH);

        JButton botaoExportarDespesas = new JButton("Exportar CSV");
        botaoExportarDespesas.setBackground(new Color(40, 167, 69));
        botaoExportarDespesas.setForeground(Color.WHITE);
        botaoExportarDespesas.setOpaque(true);
        botaoExportarDespesas.setBorderPainted(false);
        botaoExportarDespesas.setFocusPainted(false);
        painelAdicionar.add(botaoExportarDespesas);

        atualizarListaTipos(listModel);

        botaoSalvarNovo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String descricao = campoNovoTipo.getText();
                if (descricao == null || descricao.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(DespesasView.this, "A descrição não pode ser vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    TipoDespesa novo = new TipoDespesa(descricao.trim(), StatusTipoDespesa.ATIVO);
                    tipoDespesaController.salvar(novo);
                    campoNovoTipo.setText("");
                    atualizarListaTipos(listModel);
                    atualizarDados();
                } catch (ValidacaoException ex) {
                    JOptionPane.showMessageDialog(DespesasView.this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botaoEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TipoDespesa tipoSelecionado = listaTiposDespesa.getSelectedValue();
                if (tipoSelecionado == null) {
                    JOptionPane.showMessageDialog(DespesasView.this, "Selecione um tipo de despesa para editar.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                JDialog janelaEdicao = new JDialog(DespesasView.this, "Editar Tipo de Despesa", true);
                janelaEdicao.setContentPane(criarPainelEdicaoTipoDespesa(tipoSelecionado, janelaEdicao, listModel));
                janelaEdicao.pack();
                janelaEdicao.setLocationRelativeTo(DespesasView.this);
                janelaEdicao.setVisible(true);
            }
        });

        botaoExportarDespesas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salvar CSV - Tipos de Despesa");
                fileChooser.setSelectedFile(new File("despesas.csv"));
                if (fileChooser.showSaveDialog(DespesasView.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File arquivo = fileChooser.getSelectedFile();
                        String caminho = arquivo.getAbsolutePath().endsWith(".csv")
                                ? arquivo.getAbsolutePath()
                                : arquivo.getAbsolutePath() + ".csv";
                        CsvExporter.exportarTiposDespesa(tipoDespesaController.listarTodos(), caminho);
                        JOptionPane.showMessageDialog(DespesasView.this, "Exportado com sucesso!\n" + caminho);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DespesasView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        return painelPrincipal;
    }

    private void atualizarListaTipos(DefaultListModel<TipoDespesa> listModel) {
        listModel.clear();
        List<TipoDespesa> tipos = tipoDespesaController.listarTodos();
        for (TipoDespesa td : tipos) {
            listModel.addElement(td);
        }
    }

    private void registrarNovaMovimentacao() {
        try {
            Veiculo veiculo = (Veiculo) comboBoxVeiculos.getSelectedItem();
            TipoDespesa tipo = (TipoDespesa) comboBoxTiposDespesa.getSelectedItem();
            LocalDate data = campoData.getSelectedDate();
            BigDecimal valor = extrairValor(campoValor.getText());
            String descricao = campoDescricao.getText();

            Movimentacao novaMovimentacao = new Movimentacao(veiculo, tipo, descricao, data, valor);
            movimentacaoController.salvar(novaMovimentacao);
            JOptionPane.showMessageDialog(this, "Despesa registrada com sucesso! Status: PENDENTE");
            limparCamposCadastro();
            atualizarDados();
            atualizarTabelaPendentes();
        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar despesa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCamposCadastro() {
        campoValor.setText("");
        campoDescricao.setText("");
        campoData.setDate(LocalDate.now());
        if (comboBoxVeiculos.getItemCount() > 0) comboBoxVeiculos.setSelectedIndex(0);
        if (comboBoxTiposDespesa.getItemCount() > 0) comboBoxTiposDespesa.setSelectedIndex(0);
    }

    private void atualizarDados() {
        comboBoxVeiculos.removeAllItems();
        comboBoxVeiculos.addItem(null);
        List<Veiculo> veiculos = veiculoController.listarAtivos();
        for (Veiculo v : veiculos) {
            comboBoxVeiculos.addItem(v);
        }

        comboBoxTiposDespesa.removeAllItems();
        comboBoxTiposDespesa.addItem(null);
        List<TipoDespesa> tipos = tipoDespesaController.listarAtivos();
        for (TipoDespesa td : tipos) {
            comboBoxTiposDespesa.addItem(td);
        }

        if (tableModelMovimentacoes != null) {
            tableModelMovimentacoes.setRowCount(0);
            List<Movimentacao> movimentacoes = movimentacaoController.listarAprovadas();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            for (Movimentacao mov : movimentacoes) {
                tableModelMovimentacoes.addRow(new Object[]{
                        mov.getId(),
                        mov.getData().format(formatter),
                        mov.getVeiculo().getPlaca(),
                        mov.getTipoDespesa().getDescricao(),
                        mov.getDescricao(),
                        currencyFormatter.format(mov.getValor())
                });
            }
        }
    }

    private JPanel criarPainelEdicao(Movimentacao movimentacaoParaEditar, JDialog janelaPai) {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<Veiculo> editComboBoxVeiculos = new JComboBox<Veiculo>();
        JComboBox<TipoDespesa> editComboBoxTiposDespesa = new JComboBox<TipoDespesa>();
        JTextField editCampoValor = new JTextField(10);
        DatePicker editCampoData = new DatePicker(movimentacaoParaEditar.getData());
        JTextField editCampoDescricao = new JTextField(25);
        JButton botaoSalvarAlteracoes = new JButton("Salvar Alterações");

        editComboBoxVeiculos.setRenderer(comboBoxVeiculos.getRenderer());

        aplicarFiltroValor(editCampoValor);

        List<Veiculo> todosVeiculos = veiculoController.listarTodos();
        for (Veiculo v : todosVeiculos) {
            editComboBoxVeiculos.addItem(v);
            if (v.getId().equals(movimentacaoParaEditar.getVeiculo().getId())) {
                editComboBoxVeiculos.setSelectedItem(v);
            }
        }

        List<TipoDespesa> todosTipos = tipoDespesaController.listarTodos();
        for (TipoDespesa td : todosTipos) {
            editComboBoxTiposDespesa.addItem(td);
            if (td.getId().equals(movimentacaoParaEditar.getTipoDespesa().getId())) {
                editComboBoxTiposDespesa.setSelectedItem(td);
            }
        }

        // Data já definida no construtor do DatePicker
        editCampoValor.setText(movimentacaoParaEditar.getValor().toPlainString());
        editCampoDescricao.setText(movimentacaoParaEditar.getDescricao());

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Veículo:"), gbc);
        gbc.gridy++; painel.add(new JLabel("Tipo de Despesa:"), gbc);
        gbc.gridy++; painel.add(new JLabel("Data:"), gbc);
        gbc.gridy++; painel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridy++; painel.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(editComboBoxVeiculos, gbc);
        gbc.gridy++; painel.add(editComboBoxTiposDespesa, gbc);
        gbc.gridy++; painel.add(editCampoData, gbc);
        gbc.gridy++; painel.add(editCampoValor, gbc);
        gbc.gridy++; painel.add(editCampoDescricao, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(botaoSalvarAlteracoes, gbc);

        botaoSalvarAlteracoes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Veiculo veiculoSelecionado = (Veiculo) editComboBoxVeiculos.getSelectedItem();
                    TipoDespesa tipoSelecionado = (TipoDespesa) editComboBoxTiposDespesa.getSelectedItem();
                    LocalDate data = editCampoData.getSelectedDate();
                    BigDecimal valor = extrairValor(editCampoValor.getText());
                    String descricao = editCampoDescricao.getText();

                    Movimentacao movimentacaoAtualizada = new Movimentacao(veiculoSelecionado, tipoSelecionado, descricao, data, valor);
                    movimentacaoAtualizada.setId(movimentacaoParaEditar.getId());
                    movimentacaoAtualizada.setStatusMovimentacao(movimentacaoParaEditar.getStatusMovimentacao());

                    movimentacaoController.atualizar(movimentacaoAtualizada);
                    JOptionPane.showMessageDialog(janelaPai, "Despesa atualizada com sucesso!");
                    janelaPai.dispose();
                    atualizarDados();
                    atualizarTabelaPendentes();
                } catch (ValidacaoException ex) {
                    JOptionPane.showMessageDialog(janelaPai, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(janelaPai, "Erro ao atualizar despesa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return painel;
    }

    private JPanel criarPainelEdicaoTipoDespesa(TipoDespesa tipoParaEditar, JDialog janelaPai, DefaultListModel<TipoDespesa> listModel) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField campoDescricao = new JTextField(20);
        JRadioButton botaoAtivo = new JRadioButton("Status - ATIVO");
        JRadioButton botaoInativo = new JRadioButton("Status - INATIVO");
        JButton botaoSalvarAlteracoes = new JButton("Salvar Alterações");

        campoDescricao.setText(tipoParaEditar.getDescricao());

        ButtonGroup grupoStatus = new ButtonGroup();
        grupoStatus.add(botaoAtivo);
        grupoStatus.add(botaoInativo);

        if (tipoParaEditar.getStatusTipoDespesa() == StatusTipoDespesa.ATIVO) {
            botaoAtivo.setSelected(true);
        } else {
            botaoInativo.setSelected(true);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campoDescricao, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(new JLabel("Status:"), gbc);

        JPanel painelStatus = new JPanel();
        painelStatus.add(botaoAtivo);
        painelStatus.add(new JLabel("|"));
        painelStatus.add(botaoInativo);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(painelStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botaoSalvarAlteracoes, gbc);

        botaoSalvarAlteracoes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String novaDescricao = campoDescricao.getText();
                if (novaDescricao == null || novaDescricao.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(janelaPai, "A descrição não pode ser vazia.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    StatusTipoDespesa status = botaoAtivo.isSelected() ? StatusTipoDespesa.ATIVO : StatusTipoDespesa.INATIVO;
                    tipoParaEditar.setDescricao(novaDescricao.trim());
                    tipoParaEditar.setStatusTipoDespesa(status);
                    tipoDespesaController.atualizar(tipoParaEditar);
                    JOptionPane.showMessageDialog(janelaPai, "Tipo de despesa atualizado com sucesso!");
                    janelaPai.dispose();
                    atualizarListaTipos(listModel);
                    atualizarDados();
                } catch (ValidacaoException ex) {
                    JOptionPane.showMessageDialog(janelaPai, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(janelaPai, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return painel;
    }
}