package view.gui;

import controller.MovimentacaoController;
import controller.TipoDespesaController;
import controller.VeiculoController;
import exceptions.ValidacaoException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import model.enums.StatusTipoDespesa;
import view.util.CaixaAltaComLimiteFilter;

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
    private JTextField campoData;
    private JTextField campoDescricao;
    private JTextField campoQuilometragemAtual; // NOVO: Campo para quilometragem atual
    private DefaultTableModel tableModelMovimentacoes;

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

    // NOVO: Método para aplicar filtro de inteiros positivos
    private void aplicarFiltroInteiroPositivo(JTextField campo) {
        AbstractDocument doc = (AbstractDocument) campo.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String textoAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String textoResultante = textoAtual.substring(0, offset) + string + textoAtual.substring(offset);
                // Permite apenas dígitos, não permite começar com zero se houver mais de um dígito, e permite string vazia
                if (textoResultante.matches("\\d*") && (textoResultante.length() <= 1 || !textoResultante.startsWith("0"))) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String textoAtual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String textoResultante = textoAtual.substring(0, offset) + text + textoAtual.substring(offset + length);
                // Permite apenas dígitos, não permite começar com zero se houver mais de um dígito, e permite string vazia
                if (textoResultante.matches("\\d*") && (textoResultante.length() <= 1 || !textoResultante.startsWith("0"))) {
                    super.replace(fb, offset, length, text, attrs);
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
        abas.addTab("Tipos de Despesa", criarPainelCadastroTipoDespesa());

        atualizarDados();

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
                }
                return this;
            }
        });

        comboBoxTiposDespesa = new JComboBox<TipoDespesa>();
        campoValor = new JTextField(10);
        campoData = new JTextField(10);
        campoData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        campoDescricao = new JTextField(25);
        campoQuilometragemAtual = new JTextField(10); // NOVO: Inicialização do campo de quilometragem
        JButton botaoSalvar = new JButton("Salvar Despesa");

        aplicarFiltroValor(campoValor);
        aplicarFiltroInteiroPositivo(campoQuilometragemAtual); // NOVO: Aplicar filtro para inteiros positivos

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Veículo:"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Tipo de Despesa:"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Data (dd/MM/yyyy):"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Descrição:"), gbc);
        // NOVO: Label para quilometragem
        gbc.gridy++;
        JLabel labelQuilometragem = new JLabel("KM Atual do Veículo:");
        labelQuilometragem.setVisible(false); // Inicialmente invisível
        painel.add(labelQuilometragem, gbc);


        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(comboBoxVeiculos, gbc);
        gbc.gridy++;
        painel.add(comboBoxTiposDespesa, gbc);
        gbc.gridy++;
        painel.add(campoData, gbc);
        gbc.gridy++;
        painel.add(campoValor, gbc);
        gbc.gridy++;
        painel.add(campoDescricao, gbc);
        // NOVO: Campo de quilometragem
        gbc.gridy++;
        campoQuilometragemAtual.setVisible(false); // Inicialmente invisível
        painel.add(campoQuilometragemAtual, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(botaoSalvar, gbc);

        // NOVO: Listener para o comboBoxTiposDespesa para controlar a visibilidade do campo de quilometragem
        comboBoxTiposDespesa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TipoDespesa selectedTipo = (TipoDespesa) comboBoxTiposDespesa.getSelectedItem();
                boolean isCombustivel = selectedTipo != null && "COMBUSTÍVEL".equalsIgnoreCase(selectedTipo.getDescricao());
                campoQuilometragemAtual.setVisible(isCombustivel);
                labelQuilometragem.setVisible(isCombustivel);
                if (!isCombustivel) {
                    campoQuilometragemAtual.setText(""); // Limpa o campo se não for combustível
                }
            }
        });

        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarNovaMovimentacao();
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

        return painel;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate data = LocalDate.parse(campoData.getText(), formatter);
            BigDecimal valor = extrairValor(campoValor.getText());
            String descricao = campoDescricao.getText();
            double quilometragem = 0.0; // Valor padrão

            // NOVO: Lógica de validação e obtenção da quilometragem
            if (tipo != null && "COMBUSTÍVEL".equalsIgnoreCase(tipo.getDescricao())) {
                if (campoQuilometragemAtual.getText().trim().isEmpty()) {
                    throw new ValidacaoException("O campo 'KM Atual do Veículo' é obrigatório para despesas de COMBUSTÍVEL.");
                }
                try {
                    quilometragem = Double.parseDouble(campoQuilometragemAtual.getText().trim());
                    if (quilometragem <= 0) {
                        throw new ValidacaoException("A quilometragem deve ser um número positivo.");
                    }
                } catch (NumberFormatException e) {
                    throw new ValidacaoException("A quilometragem deve ser um número inteiro válido.");
                }

                // Validação da quilometragem com a última registrada
                if (veiculo != null) {
                    double ultimaQuilometragem = movimentacaoController.buscarUltimaQuilometragemVeiculo(veiculo.getId());
                    if (quilometragem <= ultimaQuilometragem) {
                        throw new ValidacaoException("Quilometragem inválida. O valor informado deve ser maior que a última quilometragem registrada para este veículo (" + (int)ultimaQuilometragem + " km).");
                    }
                }
            }


            Movimentacao novaMovimentacao = new Movimentacao(veiculo, tipo, descricao, data, valor, quilometragem); // NOVO: Passando a quilometragem
            movimentacaoController.salvar(novaMovimentacao);
            JOptionPane.showMessageDialog(this, "Despesa registrada com sucesso!");
            limparCamposCadastro();
            atualizarDados();
        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar despesa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCamposCadastro() {
        campoValor.setText("");
        campoDescricao.setText("");
        campoData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        campoQuilometragemAtual.setText(""); // NOVO: Limpar campo de quilometragem
        if (comboBoxVeiculos.getItemCount() > 0) comboBoxVeiculos.setSelectedIndex(0);
        if (comboBoxTiposDespesa.getItemCount() > 0) comboBoxTiposDespesa.setSelectedIndex(0);
    }

    private void atualizarDados() {
        comboBoxVeiculos.removeAllItems();
        List<Veiculo> veiculos = veiculoController.listarAtivos();
        for (Veiculo v : veiculos) {
            comboBoxVeiculos.addItem(v);
        }

        comboBoxTiposDespesa.removeAllItems();
        List<TipoDespesa> tipos = tipoDespesaController.listarAtivos();
        for (TipoDespesa td : tipos) {
            comboBoxTiposDespesa.addItem(td);
        }

        if (tableModelMovimentacoes != null) {
            tableModelMovimentacoes.setRowCount(0);
            List<Movimentacao> movimentacoes = movimentacaoController.listarTodos();
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
        JTextField editCampoData = new JTextField(10);
        JTextField editCampoDescricao = new JTextField(25);
        JTextField editCampoQuilometragem = new JTextField(10); // NOVO: Campo de quilometragem para edição
        JButton botaoSalvarAlteracoes = new JButton("Salvar Alterações");

        editComboBoxVeiculos.setRenderer(comboBoxVeiculos.getRenderer());

        aplicarFiltroValor(editCampoValor);
        aplicarFiltroInteiroPositivo(editCampoQuilometragem); // NOVO: Aplicar filtro para inteiros positivos

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

        editCampoData.setText(movimentacaoParaEditar.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        editCampoValor.setText(movimentacaoParaEditar.getValor().toPlainString());
        editCampoDescricao.setText(movimentacaoParaEditar.getDescricao());
        editCampoQuilometragem.setText(String.valueOf((int)movimentacaoParaEditar.getQuilometragemAtual())); // NOVO: Preencher campo de quilometragem

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Veículo:"), gbc);
        gbc.gridy++; painel.add(new JLabel("Tipo de Despesa:"), gbc);
        gbc.gridy++; painel.add(new JLabel("Data (dd/MM/yyyy):"), gbc);
        gbc.gridy++; painel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridy++; painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridy++; JLabel labelEditQuilometragem = new JLabel("KM Atual do Veículo:"); painel.add(labelEditQuilometragem, gbc); // NOVO: Label para quilometragem na edição

        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(editComboBoxVeiculos, gbc);
        gbc.gridy++; painel.add(editComboBoxTiposDespesa, gbc);
        gbc.gridy++; painel.add(editCampoData, gbc);
        gbc.gridy++; painel.add(editCampoValor, gbc);
        gbc.gridy++; painel.add(editCampoDescricao, gbc);
        gbc.gridy++; painel.add(editCampoQuilometragem, gbc); // NOVO: Campo de quilometragem na edição

        // NOVO: Listener para controlar visibilidade do campo de quilometragem na edição
        editComboBoxTiposDespesa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TipoDespesa selectedTipo = (TipoDespesa) editComboBoxTiposDespesa.getSelectedItem();
                boolean isCombustivel = selectedTipo != null && "COMBUSTÍVEL".equalsIgnoreCase(selectedTipo.getDescricao());
                editCampoQuilometragem.setVisible(isCombustivel);
                labelEditQuilometragem.setVisible(isCombustivel);
                if (!isCombustivel) {
                    editCampoQuilometragem.setText("");
                }
            }
        });
        // NOVO: Chamar o listener uma vez para configurar a visibilidade inicial
        editComboBoxTiposDespesa.getActionListeners()[0].actionPerformed(new ActionEvent(editComboBoxTiposDespesa, ActionEvent.ACTION_PERFORMED, "init"));


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
                    LocalDate data = LocalDate.parse(editCampoData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    BigDecimal valor = extrairValor(editCampoValor.getText());
                    String descricao = editCampoDescricao.getText();
                    double quilometragem = 0.0; // Valor padrão

                    // NOVO: Lógica de validação e obtenção da quilometragem para edição
                    if (tipoSelecionado != null && "COMBUSTÍVEL".equalsIgnoreCase(tipoSelecionado.getDescricao())) {
                        if (editCampoQuilometragem.getText().trim().isEmpty()) {
                            throw new ValidacaoException("O campo 'KM Atual do Veículo' é obrigatório para despesas de COMBUSTÍVEL.");
                        }
                        try {
                            quilometragem = Double.parseDouble(editCampoQuilometragem.getText().trim());
                            if (quilometragem <= 0) {
                                throw new ValidacaoException("A quilometragem deve ser um número positivo.");
                            }
                        } catch (NumberFormatException ex) {
                            throw new ValidacaoException("A quilometragem deve ser um número inteiro válido.");
                        }

                        // Validação da quilometragem com a última registrada (excluindo a própria movimentação se for o caso)
                        if (veiculoSelecionado != null) {
                            double ultimaQuilometragem = movimentacaoController.buscarUltimaQuilometragemVeiculoExcluindoAtual(veiculoSelecionado.getId(), movimentacaoParaEditar.getId());
                            if (quilometragem <= ultimaQuilometragem) {
                                throw new ValidacaoException("Quilometragem inválida. O valor informado deve ser maior que a última quilometragem registrada para este veículo (" + (int)ultimaQuilometragem + " km).");
                            }
                        }
                    }

                    Movimentacao movimentacaoAtualizada = new Movimentacao(veiculoSelecionado, tipoSelecionado, descricao, data, valor, quilometragem); // NOVO: Passando a quilometragem
                    movimentacaoAtualizada.setId(movimentacaoParaEditar.getId());

                    movimentacaoController.atualizar(movimentacaoAtualizada);
                    JOptionPane.showMessageDialog(janelaPai, "Despesa atualizada com sucesso!");
                    janelaPai.dispose();
                    atualizarDados();
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
