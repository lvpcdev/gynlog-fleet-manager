package view.gui;

import controller.VeiculoController;
import exceptions.ValidacaoException;
import model.entities.Veiculo;
import model.enums.StatusVeiculo;
import util.BuscaSequencial;
import util.CsvExporter;
import view.util.CaixaAltaComLimiteFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.time.Year;
import java.util.List;

public class VeiculosView extends JFrame {

    VeiculoController veiculoController = new VeiculoController();

    String[] colunas = {"ID", "Placa", "Marca", "Modelo", "Categoria", "Ano Fabricação", "Estado"};

    DefaultTableModel tableModelVeiculos = new DefaultTableModel(colunas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public VeiculosView() {
        setTitle("VEÍCULOS");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setContentPane(buildMainPanel(true));
        setVisible(true);
    }

    VeiculosView(boolean forEmbed) {
    }

    public static JPanel createMainPanel() {
        VeiculosView v = new VeiculosView(false);
        return v.buildMainPanel(false);
    }

    public JPanel getMainPanel() {
        return buildMainPanel(false);
    }

    private void atualizarTabela(List<Veiculo> veiculos) {
        tableModelVeiculos.setRowCount(0);
        for (Veiculo v : veiculos) {
            tableModelVeiculos.addRow(new Object[]{
                    v.getId(),
                    v.getPlaca(),
                    v.getMarca(),
                    v.getModelo(),
                    v.getCategoria(),
                    v.getAnoDeFabricacao(),
                    v.getStatusVeiculo()
            });
        }
    }

    private JPanel buildMainPanel(boolean includeTopBackButton) {
        JPanel container = new JPanel(new BorderLayout());

        if (includeTopBackButton) {
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton botaoVoltar = new JButton("Voltar");
            topo.add(botaoVoltar);
            container.add(topo, BorderLayout.NORTH);

            botaoVoltar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new MenuView();
                            VeiculosView.this.dispose();
                        }
                    });
                }
            });
        }

        JTabbedPane abasPrincipais = new JTabbedPane();
        container.add(abasPrincipais, BorderLayout.CENTER);

        JPanel painelCadastro = criarPainelCadastroVeiculos();
        abasPrincipais.addTab("Cadastro de Veículos", painelCadastro);

        JPanel painelListagem = criarPainelListagemVeiculos();
        abasPrincipais.addTab("Listagem de Veículos", painelListagem);

        return container;
    }

    private JPanel criarPainelCadastroVeiculos() {

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField campoPlaca = new JTextField(8);
        JTextField campoMarca = new JTextField(20);
        JTextField campoModelo = new JTextField(20);
        JTextField campoCategoria = new JTextField(20);
        JTextField campoAnoFabricacao = new JTextField(4);
        JRadioButton botaoAtivo = new JRadioButton("Estado Veículo - ATIVO");
        JRadioButton botaoInativo = new JRadioButton("Estado Veículo - INATIVO");
        JButton botaoCadastrar = new JButton("Cadastrar Veículos");
        botaoAtivo.setSelected(true);

        AbstractDocument docPlaca = (AbstractDocument) campoPlaca.getDocument();
        docPlaca.setDocumentFilter(new CaixaAltaComLimiteFilter(7));

        AbstractDocument docMarca = (AbstractDocument) campoMarca.getDocument();
        docMarca.setDocumentFilter(new CaixaAltaComLimiteFilter(50));

        AbstractDocument docModelo = (AbstractDocument) campoModelo.getDocument();
        docModelo.setDocumentFilter(new CaixaAltaComLimiteFilter(50));

        AbstractDocument docCategoria = (AbstractDocument) campoCategoria.getDocument();
        docCategoria.setDocumentFilter(new CaixaAltaComLimiteFilter(30));

        AbstractDocument docAnoFabricacao = (AbstractDocument) campoAnoFabricacao.getDocument();
        docAnoFabricacao.setDocumentFilter(new CaixaAltaComLimiteFilter(4) {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9]+") && fb.getDocument().getLength() + string.length() <= 4) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9]*") && fb.getDocument().getLength() - length + text.length() <= 4) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("PLACA:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(campoPlaca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MARCA:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        painel.add(campoMarca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MODELO:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        painel.add(campoModelo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("CATEGORIA:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        painel.add(campoCategoria, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ANO DE FABRICAÇÃO:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        painel.add(campoAnoFabricacao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ESTADO DO VEÍCULO:"), gbc);

        ButtonGroup grupoEstadoVeiculo = new ButtonGroup();
        grupoEstadoVeiculo.add(botaoAtivo);
        grupoEstadoVeiculo.add(botaoInativo);

        JPanel painelEstadoVeiculo = new JPanel();
        painelEstadoVeiculo.add(botaoAtivo);
        painelEstadoVeiculo.add(new JLabel("\t|\t"));
        painelEstadoVeiculo.add(botaoInativo);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(painelEstadoVeiculo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botaoCadastrar, gbc);

        botaoCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (campoPlaca.getText().trim().isEmpty() ||
                        campoMarca.getText().trim().isEmpty() ||
                        campoModelo.getText().trim().isEmpty() ||
                        campoCategoria.getText().trim().isEmpty() ||
                        campoAnoFabricacao.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(painel,
                            "Todos os campos são obrigatórios!\n" +
                                    "Preencha: Placa, Marca, Modelo, Categoria e Ano de Fabricação.",
                            "Campos Obrigatórios",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    StatusVeiculo status = botaoAtivo.isSelected() ? StatusVeiculo.ATIVO : StatusVeiculo.INATIVO;
                    Veiculo novoVeiculo = new Veiculo(
                            campoPlaca.getText(),
                            campoMarca.getText(),
                            campoModelo.getText(),
                            campoCategoria.getText(),
                            status,
                            Year.parse(campoAnoFabricacao.getText())
                    );
                    veiculoController.salvar(novoVeiculo);
                    JOptionPane.showMessageDialog(painel, "Veículo cadastrado com sucesso!");
                    limparCampos(campoPlaca, campoMarca, campoModelo, campoCategoria, campoAnoFabricacao, botaoAtivo);
                    atualizarTabela(veiculoController.listarTodos());
                } catch (ValidacaoException ex) {
                    JOptionPane.showMessageDialog(painel, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(painel, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return painel;
    }

    private JPanel criarPainelListagemVeiculos() {

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        atualizarTabela(veiculoController.listarTodos());

        JTable veiculosTable = new JTable(tableModelVeiculos);
        veiculosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(veiculosTable);
        painel.add(scrollPane, BorderLayout.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < veiculosTable.getColumnCount(); i++) {
            veiculosTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton botaoEditar = new JButton("Editar Veículos");
        JCheckBox checkBoxInativos = new JCheckBox("Apenas Inativos");
        JCheckBox checkBoxAtivos = new JCheckBox("Apenas Ativos");
        JTextField campoBusca = new JTextField(15);
        JButton botaoBuscarPlaca = new JButton("Buscar Placa");
        JButton botaoBuscarModelo = new JButton("Buscar Modelo");
        JButton botaoLimparBusca = new JButton("Limpar");
        JButton botaoExportarVeiculos = new JButton("Exportar CSV");
        botaoExportarVeiculos.setBackground(new Color(40, 167, 69));
        botaoExportarVeiculos.setForeground(Color.WHITE);
        botaoExportarVeiculos.setOpaque(true);
        botaoExportarVeiculos.setBorderPainted(false);
        botaoExportarVeiculos.setFocusPainted(false);

        botaoEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int linhaEscolhida = veiculosTable.getSelectedRow();
                    if (linhaEscolhida == -1) {
                        JOptionPane.showMessageDialog(VeiculosView.this, "Nenhum veículo selecionado!");
                        return;
                    }
                    Long id = Long.parseLong(tableModelVeiculos.getValueAt(linhaEscolhida, 0).toString());
                    JDialog janelaTemp = new JDialog(VeiculosView.this, "Editar Veículo", true);
                    janelaTemp.add(criarPainelEditarVeiculos(id));
                    janelaTemp.pack();
                    janelaTemp.setLocationRelativeTo(VeiculosView.this);
                    janelaTemp.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VeiculosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        checkBoxInativos.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (checkBoxAtivos.isSelected()) {
                    JOptionPane.showMessageDialog(VeiculosView.this, "Filtro de ativos selecionado!");
                    return;
                }
                if (checkBoxInativos.isSelected()) {
                    atualizarTabela(veiculoController.listarInativos());
                } else {
                    atualizarTabela(veiculoController.listarTodos());
                }
            }
        });

        checkBoxAtivos.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (checkBoxInativos.isSelected()) {
                    JOptionPane.showMessageDialog(VeiculosView.this, "Filtro de inativos selecionado!");
                    return;
                }
                if (checkBoxAtivos.isSelected()) {
                    atualizarTabela(veiculoController.listarAtivos());
                } else {
                    atualizarTabela(veiculoController.listarTodos());
                }
            }
        });

        botaoBuscarPlaca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String termo = campoBusca.getText().trim();
                if (termo.isEmpty()) {
                    JOptionPane.showMessageDialog(VeiculosView.this, "Digite algo para buscar!");
                    return;
                }
                List<Veiculo> resultado = BuscaSequencial.buscarPorPlaca(veiculoController.listarTodos(), termo);
                if (resultado.isEmpty()) {
                    JOptionPane.showMessageDialog(VeiculosView.this, "Nenhum veículo encontrado!");
                    return;
                }
                atualizarTabela(resultado);
            }
        });

        botaoBuscarModelo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String termo = campoBusca.getText().trim();
                if (termo.isEmpty()) {
                    JOptionPane.showMessageDialog(VeiculosView.this, "Digite algo para buscar!");
                    return;
                }
                List<Veiculo> resultado = BuscaSequencial.buscarPorModelo(veiculoController.listarTodos(), termo);
                if (resultado.isEmpty()) {
                    JOptionPane.showMessageDialog(VeiculosView.this, "Nenhum veículo encontrado!");
                    return;
                }
                atualizarTabela(resultado);
            }
        });

        botaoLimparBusca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                campoBusca.setText("");
                atualizarTabela(veiculoController.listarTodos());
            }
        });

        botaoExportarVeiculos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salvar CSV - Veículos");
                fileChooser.setSelectedFile(new File("veiculos.csv"));
                if (fileChooser.showSaveDialog(VeiculosView.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File arquivo = fileChooser.getSelectedFile();
                        String caminho = arquivo.getAbsolutePath().endsWith(".csv")
                                ? arquivo.getAbsolutePath()
                                : arquivo.getAbsolutePath() + ".csv";
                        CsvExporter.exportarVeiculos(veiculoController.listarTodos(), caminho);
                        JOptionPane.showMessageDialog(VeiculosView.this, "Exportado com sucesso!\n" + caminho);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(VeiculosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        painel.add(painelBotoes, BorderLayout.SOUTH);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(checkBoxInativos);
        painelBotoes.add(checkBoxAtivos);
        painelBotoes.add(new JLabel("Buscar:"));
        painelBotoes.add(campoBusca);
        painelBotoes.add(botaoBuscarPlaca);
        painelBotoes.add(botaoBuscarModelo);
        painelBotoes.add(botaoLimparBusca);
        painelBotoes.add(botaoExportarVeiculos);

        return painel;
    }

    private JPanel criarPainelEditarVeiculos(Long id) {

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField campoPlaca = new JTextField(8);
        JTextField campoMarca = new JTextField(20);
        JTextField campoModelo = new JTextField(20);
        JTextField campoCategoria = new JTextField(20);
        JTextField campoAnoFabricacao = new JTextField(4);
        JRadioButton botaoAtivo = new JRadioButton("Estado Veículo - ATIVO");
        JRadioButton botaoInativo = new JRadioButton("Estado Veículo - INATIVO");
        JButton botaoEditar = new JButton("Editar Veículo");

        botaoAtivo.setSelected(true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("PLACA:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(campoPlaca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MARCA:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        painel.add(campoMarca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MODELO:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        painel.add(campoModelo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("CATEGORIA:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        painel.add(campoCategoria, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ANO DE FABRICAÇÃO:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        painel.add(campoAnoFabricacao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ESTADO DO VEÍCULO:"), gbc);

        ButtonGroup grupoEstadoVeiculo = new ButtonGroup();
        grupoEstadoVeiculo.add(botaoAtivo);
        grupoEstadoVeiculo.add(botaoInativo);

        JPanel painelEstadoVeiculo = new JPanel();
        painelEstadoVeiculo.add(botaoAtivo);
        painelEstadoVeiculo.add(new JLabel("\t|\t"));
        painelEstadoVeiculo.add(botaoInativo);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(painelEstadoVeiculo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botaoEditar, gbc);

        Veiculo veiculo = veiculoController.buscarPorId(id);
        campoPlaca.setText(veiculo.getPlaca());
        campoMarca.setText(veiculo.getMarca());
        campoModelo.setText(veiculo.getModelo());
        campoCategoria.setText(veiculo.getCategoria());
        campoAnoFabricacao.setText(veiculo.getAnoDeFabricacao().toString());
        if (veiculo.getStatusVeiculo() == StatusVeiculo.ATIVO) {
            botaoAtivo.setSelected(true);
        } else {
            botaoInativo.setSelected(true);
        }

        botaoEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    StatusVeiculo status = botaoAtivo.isSelected() ? StatusVeiculo.ATIVO : StatusVeiculo.INATIVO;
                    Veiculo veiculoAtualizado = new Veiculo(
                            campoPlaca.getText(),
                            campoMarca.getText(),
                            campoModelo.getText(),
                            campoCategoria.getText(),
                            status,
                            Year.parse(campoAnoFabricacao.getText())
                    );
                    veiculoAtualizado.setId(id);
                    veiculoController.atualizar(veiculoAtualizado);
                    atualizarTabela(veiculoController.listarTodos());
                    JOptionPane.showMessageDialog(painel, "Veículo alterado com sucesso!");
                    SwingUtilities.getWindowAncestor(painel).dispose();
                } catch (ValidacaoException ex) {
                    JOptionPane.showMessageDialog(painel, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(painel, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return painel;
    }

    private void limparCampos(JTextField campoPlaca, JTextField campoMarca,
                              JTextField campoModelo, JTextField campoCategoria,
                              JTextField campoAnoFabricacao, JRadioButton botaoAtivo) {
        campoPlaca.setText("");
        campoMarca.setText("");
        campoModelo.setText("");
        campoCategoria.setText("");
        campoAnoFabricacao.setText("");
        botaoAtivo.setSelected(true);
    }

    public void refreshData() {
        atualizarTabela(veiculoController.listarTodos());
    }
}