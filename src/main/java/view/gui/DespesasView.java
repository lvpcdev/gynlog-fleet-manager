package view.gui;

import controller.MovimentacaoController;
import controller.TipoDespesaController;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import persistence.dao.VeiculoDAO; // Usado para popular a lista de veículos

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DespesasView extends JFrame {

    // --- Controladores ---
    private final MovimentacaoController movimentacaoController;
    private final TipoDespesaController tipoDespesaController;

    // --- Componentes da UI ---
    private JComboBox<Veiculo> comboBoxVeiculos;
    private JComboBox<TipoDespesa> comboBoxTiposDespesa;
    private JTextField campoValor;
    private JTextField campoData; // Usaremos um campo de texto simples para a data por enquanto
    private JTextField campoDescricao;
    private DefaultTableModel tableModelMovimentacoes;

    public DespesasView() {
        this.movimentacaoController = new MovimentacaoController();
        this.tipoDespesaController = new TipoDespesaController();

        setTitle("GESTÃO DE DESPESAS");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // DISPOSE_ON_CLOSE para não fechar a aplicação inteira
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        this.add(abas, BorderLayout.CENTER);

        // --- Aba 1: Cadastro de Movimentação ---
        JPanel painelCadastroMovimentacao = criarPainelCadastroMovimentacao();
        abas.addTab("Registrar Despesa", painelCadastroMovimentacao);

        // --- Aba 2: Listagem de Movimentações ---
        JPanel painelListagem = criarPainelListagem();
        abas.addTab("Histórico de Despesas", painelListagem);

        // --- Aba 3: Cadastro de Tipos de Despesa ---
        JPanel painelCadastroTipo = criarPainelCadastroTipoDespesa();
        abas.addTab("Tipos de Despesa", painelCadastroTipo);

        // Carrega os dados iniciais nas tabelas e combos
        atualizarDados();

        setVisible(true);
    }


     // Painel principal para registrar uma nova despesa.

    private JPanel criarPainelCadastroMovimentacao() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // --- Componentes ---
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
        campoData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // Sugere a data atual
        campoDescricao = new JTextField(25);
        JButton botaoSalvar = new JButton("Salvar Despesa");

        // --- Layout ---
        // Rótulos
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

        // Campos
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

        // Botão
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(botaoSalvar, gbc);

        // --- Ação do Botão ---
        botaoSalvar.addActionListener(e -> registrarNovaMovimentacao());

        return painel;
    }


     //Painel para listar todas as movimentações em uma tabela.

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

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botaoExcluir = new JButton("Excluir Despesa Selecionada");
        painelBotoes.add(botaoExcluir);
        painel.add(painelBotoes, BorderLayout.SOUTH);

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


     // Painel para cadastrar um novo tipo de despesa.

    private JPanel criarPainelCadastroTipoDespesa() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        JTextField campoNovoTipo = new JTextField(20);
        JButton botaoSalvarTipo = new JButton("Salvar Novo Tipo");

        painel.add(new JLabel("Nova Descrição:"));
        painel.add(campoNovoTipo);
        painel.add(botaoSalvarTipo);

        botaoSalvarTipo.addActionListener(e -> {
            String descricao = campoNovoTipo.getText();
            if (descricao != null && !descricao.trim().isEmpty()) {
                tipoDespesaController.criarTipoDespesa(descricao);
                JOptionPane.showMessageDialog(this, "Tipo de despesa '" + descricao + "' salvo com sucesso!");
                campoNovoTipo.setText("");
                atualizarDados();
            } else {
                JOptionPane.showMessageDialog(this, "A descrição não pode ser vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
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


        tableModelMovimentacoes.setRowCount(0); // Limpa a tabela
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

    public static void main(String[] args) {
        // Garante que a UI seja criada na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            new DespesasView();
        });
    }
}
