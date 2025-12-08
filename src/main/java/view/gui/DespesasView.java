package view.gui;

import controller.MovimentacaoController;
import controller.TipoDespesaController;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import persistence.dao.VeiculoDAO;
import view.util.CaixaAltaComLimiteFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DespesasView extends JFrame {

    private final MovimentacaoController movimentacaoController;
    private final TipoDespesaController tipoDespesaController;


    private JComboBox<Veiculo> comboBoxVeiculos;
    private JComboBox<TipoDespesa> comboBoxTiposDespesa;
    private JTextField campoValor;
    private JTextField campoData;
    private JTextField campoDescricao;
    private DefaultTableModel tableModelMovimentacoes;

    public DespesasView() {
        this.movimentacaoController = new MovimentacaoController();
        this.tipoDespesaController = new TipoDespesaController();
    }


    public JPanel getMainPanel() {
        return buildMainPanel(false);
    }

    public void refreshData() {
        atualizarDados();
    }


    private JPanel buildMainPanel(boolean includeTopBackButton) {
        JPanel root = new JPanel(new BorderLayout());

        if (includeTopBackButton) {
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton botaoVoltar = new JButton("Voltar");
            topo.add(botaoVoltar);
            root.add(topo, BorderLayout.NORTH);

            botaoVoltar.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new MenuView();
                    DespesasView.this.dispose();
                });
            });
        }


        JTabbedPane abas = new JTabbedPane();
        root.add(abas, BorderLayout.CENTER);


        JPanel painelCadastroMovimentacao = criarPainelCadastroMovimentacao();
        abas.addTab("Registrar Despesa", painelCadastroMovimentacao);

        JPanel painelListagem = criarPainelListagem();
        abas.addTab("Histórico de Despesas", painelListagem);

        JPanel painelCadastroTipo = criarPainelCadastroTipoDespesa();
        abas.addTab("Tipos de Despesa", painelCadastroTipo);

        atualizarDados();

        return root;
    }



    private JPanel criarPainelCadastroMovimentacao() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;


        comboBoxVeiculos = new JComboBox<>();
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
        comboBoxTiposDespesa = new JComboBox<>();
        campoValor = new JTextField(10);
        campoData = new JTextField(10);
        campoData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        campoDescricao = new JTextField(25);
        JButton botaoSalvar = new JButton("Salvar Despesa");


        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Veículo:"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Tipo de Despesa:"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Data (dd/MM/yyyy):"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridy++;
        painel.add(new JLabel("Descrição:"), gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
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


        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(botaoSalvar, gbc);


        botaoSalvar.addActionListener(e -> registrarNovaMovimentacao());

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

        botaoEditar.addActionListener(e -> {
            int selectedRow = tabela.getSelectedRow();
            if (selectedRow >= 0) {
                Long idMovimentacao = (Long) tableModelMovimentacoes.getValueAt(selectedRow, 0);

                Movimentacao movimentacaoParaEditar = movimentacaoController.buscarMovimentacaoPorId(idMovimentacao);

                if (movimentacaoParaEditar != null) {

                    JDialog janelaEdicao = new JDialog(this, "Editar Despesa", true);
                    janelaEdicao.setContentPane(criarPainelEdicao(movimentacaoParaEditar, janelaEdicao));
                    janelaEdicao.pack();
                    janelaEdicao.setLocationRelativeTo(this);
                    janelaEdicao.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível encontrar os dados da despesa para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma despesa na tabela para editar.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
            }
        });


        botaoExcluir.addActionListener(e -> {
            int selectedRow = tabela.getSelectedRow();
            if (selectedRow >= 0) {
                Long id = (Long) tableModelMovimentacoes.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a despesa ID " + id + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    movimentacaoController.excluirMovimentacao(id);
                    atualizarDados();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma despesa na tabela para excluir.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
            }
        });

        return painel;
    }



    private JPanel criarPainelCadastroTipoDespesa() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<TipoDespesa> listModel = new DefaultListModel<>();
        JList<TipoDespesa> listaTiposDespesa = new JList<>(listModel);
        listaTiposDespesa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        painelPrincipal.add(new JScrollPane(listaTiposDespesa), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        JButton botaoEditar = new JButton("Editar");
        JButton botaoExcluir = new JButton("Excluir");


        botaoEditar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaoExcluir.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelBotoes.add(botaoEditar);
        painelBotoes.add(Box.createRigidArea(new Dimension(0, 5)));
        painelBotoes.add(botaoExcluir);

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

        Runnable atualizarLista = () -> {
            listModel.clear();
            tipoDespesaController.listarTodosTiposDespesa().forEach(listModel::addElement);
        };


        atualizarLista.run();




        botaoSalvarNovo.addActionListener(e -> {
            String descricao = campoNovoTipo.getText();
            if (descricao != null && !descricao.trim().isEmpty()) {
                tipoDespesaController.criarTipoDespesa(descricao);
                campoNovoTipo.setText("");
                atualizarLista.run();
                atualizarDados();
            } else {
                JOptionPane.showMessageDialog(this, "A descrição não pode ser vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });


        botaoEditar.addActionListener(e -> {
            TipoDespesa tipoSelecionado = listaTiposDespesa.getSelectedValue();
            if (tipoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um tipo de despesa para editar.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JDialog janelaEdicao = new JDialog(this, "Editar Tipo de Despesa", true);

            janelaEdicao.setContentPane(criarPainelEdicaoTipoDespesa(tipoSelecionado, janelaEdicao, atualizarLista));

            janelaEdicao.pack();
            janelaEdicao.setLocationRelativeTo(this);
            janelaEdicao.setVisible(true);
        });


        botaoExcluir.addActionListener(e -> {
            TipoDespesa tipoSelecionado = listaTiposDespesa.getSelectedValue();
            if (tipoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um tipo de despesa para excluir.", "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o tipo '" + tipoSelecionado.getDescricao() + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {

                boolean sucesso = tipoDespesaController.excluirTipoDespesa(tipoSelecionado.getIdTipoDespesa());


                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Tipo de despesa excluído com sucesso!");
                    atualizarLista.run();
                    atualizarDados();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Não foi possível excluir. Este tipo de despesa já está sendo usado em um ou mais registros.",
                            "Operação Bloqueada",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return painelPrincipal;
    }


    private void registrarNovaMovimentacao() {
        try {
            Veiculo veiculo = (Veiculo) comboBoxVeiculos.getSelectedItem();
            TipoDespesa tipo = (TipoDespesa) comboBoxTiposDespesa.getSelectedItem();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate data = LocalDate.parse(campoData.getText(), formatter);

            BigDecimal valor = new BigDecimal(campoValor.getText().replace(",", "."));
            String descricao = campoDescricao.getText();



            boolean sucesso = movimentacaoController.registrarMovimentacao(veiculo, tipo, descricao, data, valor);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Despesa registrada com sucesso!");
                limparCamposCadastro();
                atualizarDados();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao registrar despesa. Verifique os dados e tente novamente.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar despesa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void limparCamposCadastro() {
        campoValor.setText("");
        campoDescricao.setText("");
        campoData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        comboBoxVeiculos.setSelectedIndex(0);
        comboBoxTiposDespesa.setSelectedIndex(0);
    }

    private void atualizarDados() {

        comboBoxVeiculos.removeAllItems();
        new VeiculoDAO().listarTodos().forEach(comboBoxVeiculos::addItem);


        comboBoxTiposDespesa.removeAllItems();
        tipoDespesaController.listarTodosTiposDespesa().forEach(comboBoxTiposDespesa::addItem);


        tableModelMovimentacoes.setRowCount(0);
        List<Movimentacao> movimentacoes = movimentacaoController.listarTodasMovimentacoes();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        for (Movimentacao mov : movimentacoes) {
            tableModelMovimentacoes.addRow(new Object[]{
                    mov.getIdMovimentacao(),
                    mov.getData().format(formatter),
                    mov.getVeiculo().getPlaca(),
                    mov.getTipoDespesa().getDescricao(),
                    mov.getDescricao(),
                    currencyFormatter.format(mov.getValor())
            });
        }
    }

    private JPanel criarPainelEdicao(Movimentacao movimentacaoParaEditar, JDialog janelaPai) {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;


        JComboBox<Veiculo> editComboBoxVeiculos = new JComboBox<>();
        JComboBox<TipoDespesa> editComboBoxTiposDespesa = new JComboBox<>();
        JTextField editCampoValor = new JTextField(10);
        JTextField editCampoData = new JTextField(10);
        JTextField editCampoDescricao = new JTextField(25);
        JButton botaoSalvarAlteracoes = new JButton("Salvar Alterações");


        List<TipoDespesa> todosTipos = tipoDespesaController.listarTodosTiposDespesa();
        for (TipoDespesa td : todosTipos) {
            editComboBoxTiposDespesa.addItem(td);
            if (td.getIdTipoDespesa().equals(movimentacaoParaEditar.getTipoDespesa().getIdTipoDespesa())) {
                editComboBoxTiposDespesa.setSelectedItem(td);
            }
        }

        List<Veiculo> todosVeiculos = new VeiculoDAO().listarTodos();
        for (Veiculo v : todosVeiculos) {
            editComboBoxVeiculos.addItem(v);
            if (v.getIdVeiculo().equals(movimentacaoParaEditar.getVeiculo().getIdVeiculo())) {
                editComboBoxVeiculos.setSelectedItem(v);
            }
        }

        editComboBoxVeiculos.setRenderer(comboBoxVeiculos.getRenderer());

        editCampoData.setText(movimentacaoParaEditar.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        editCampoValor.setText(movimentacaoParaEditar.getValor().toPlainString());
        editCampoDescricao.setText(movimentacaoParaEditar.getDescricao());


        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Veículo:"), gbc);
        gbc.gridy++; painel.add(new JLabel("Tipo de Despesa:"), gbc);
        gbc.gridy++; painel.add(new JLabel("Data (dd/MM/yyyy):"), gbc);
        gbc.gridy++; painel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridy++; painel.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(editComboBoxVeiculos, gbc);
        gbc.gridy++; painel.add(editComboBoxTiposDespesa, gbc);
        gbc.gridy++; painel.add(editCampoData, gbc);
        gbc.gridy++; painel.add(editCampoValor, gbc);
        gbc.gridy++; painel.add(editCampoDescricao, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        painel.add(botaoSalvarAlteracoes, gbc);


        botaoSalvarAlteracoes.addActionListener(e -> {
            try {

                Veiculo veiculoSelecionado = (Veiculo) editComboBoxVeiculos.getSelectedItem();
                TipoDespesa tipoSelecionado = (TipoDespesa) editComboBoxTiposDespesa.getSelectedItem();
                LocalDate data = LocalDate.parse(editCampoData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                BigDecimal valor = new BigDecimal(editCampoValor.getText().replace(",", "."));
                String descricao = editCampoDescricao.getText();


                Movimentacao movimentacaoAtualizada = new Movimentacao(veiculoSelecionado, tipoSelecionado, descricao, data, valor);


                movimentacaoAtualizada.setIdMovimentacao(movimentacaoParaEditar.getIdMovimentacao());


                boolean sucesso = movimentacaoController.atualizarMovimentacao(movimentacaoAtualizada);

                if (sucesso) {
                    JOptionPane.showMessageDialog(janelaPai, "Despesa atualizada com sucesso!");
                    janelaPai.dispose();
                    atualizarDados();
                } else {
                    JOptionPane.showMessageDialog(janelaPai, "Falha ao atualizar. Verifique os dados inseridos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(janelaPai, "Erro ao atualizar despesa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }

    private JPanel criarPainelEdicaoTipoDespesa(TipoDespesa tipoParaEditar, JDialog janelaPai, Runnable acaoAposSalvar) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);


        JTextField campoDescricao = new JTextField(20);
        JButton botaoSalvarAlteracoes = new JButton("Salvar Alterações");

        campoDescricao.setText(tipoParaEditar.getDescricao());


        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Nova Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(campoDescricao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botaoSalvarAlteracoes, gbc);


        botaoSalvarAlteracoes.addActionListener(e -> {
            String novaDescricao = campoDescricao.getText();

            if (novaDescricao == null || novaDescricao.trim().isEmpty()) {
                JOptionPane.showMessageDialog(janelaPai, "A descrição não pode ser vazia.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TipoDespesa tipoAtualizado = new TipoDespesa(novaDescricao.trim());


            tipoAtualizado.setIdTipoDespesa(tipoParaEditar.getIdTipoDespesa());

            tipoDespesaController.atualizarTipoDespesa(tipoAtualizado);


            JOptionPane.showMessageDialog(janelaPai, "Tipo de despesa atualizado com sucesso!");
            janelaPai.dispose();


            acaoAposSalvar.run();
            atualizarDados();
        });

        return painel;
    }


}
