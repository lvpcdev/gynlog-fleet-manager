package view.gui;

import controller.VeiculoController;
import model.enums.StatusVeiculo;
import persistence.dao.VeiculoDAO;
import model.entities.Veiculo;
import view.util.CaixaAltaComLimiteFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;


public class VeiculosView extends JFrame {

    VeiculoController veiculoController = new VeiculoController();
    String[] colunas = {"ID", "Placa", "Marca", "Modelo", "Ano Fabricação", "Estado"};
    VeiculoDAO veiculoDAO = new VeiculoDAO();

    DefaultTableModel tableModelVeiculos = new DefaultTableModel(colunas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {

            return false;
        }
    };

    public VeiculosView() {
        setTitle("VEÍCULOS");
        setSize(800, 600);
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


    private JPanel buildMainPanel(boolean includeTopBackButton) {
        JPanel container = new JPanel(new BorderLayout());

        if (includeTopBackButton) {
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton botaoVoltar = new JButton("Voltar");
            topo.add(botaoVoltar);
            container.add(topo, BorderLayout.NORTH);

            botaoVoltar.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new MenuView();
                    VeiculosView.this.dispose();
                });
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
        gbc.insets = new Insets(8,8,8,8);

        JTextField campoPlaca = new JTextField(8);
        JTextField campoMarca = new JTextField(20);
        JTextField campoModelo = new JTextField(20);
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

        AbstractDocument docAnoFabricacao = (AbstractDocument) campoAnoFabricacao.getDocument();
        docAnoFabricacao.setDocumentFilter(new CaixaAltaComLimiteFilter(4));


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("PLACA:"),gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(campoPlaca,gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MARCA:"),gbc);


        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        painel.add(campoMarca,gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MODELO:"),gbc);


        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        painel.add(campoModelo,gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ANO DE FABRICAÇÃO:"),gbc);


        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        painel.add(campoAnoFabricacao,gbc);


        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ESTADO DO VEÍCULO:"),gbc);


        ButtonGroup grupoEstadoVeiculo = new ButtonGroup();
        grupoEstadoVeiculo.add(botaoAtivo);
        grupoEstadoVeiculo.add(botaoInativo);


        JPanel painelEstadoVeiculo = new JPanel();
        painelEstadoVeiculo.add(botaoAtivo);
        painelEstadoVeiculo.add(new JLabel("\t|\t"));
        painelEstadoVeiculo.add(botaoInativo);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(painelEstadoVeiculo,gbc);


        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botaoCadastrar,gbc);


        botaoCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String placa = campoPlaca.getText();
                String marca = campoMarca.getText();
                String modelo = campoModelo.getText();
                String ano = campoAnoFabricacao.getText();
                int linhaAtual = 1;

                while (linhaAtual <= veiculoDAO.getQuantidadeDeVeiculos()) {
                    if (veiculoDAO.LerVeiculos("placaVeiculo", linhaAtual).equals(placa)) {
                        JOptionPane.showMessageDialog(painel,
                                "Existe uma placa idêntica a que você colocou, por favor, reescrever!",
                                "ERRO DE VALIDAÇÃO!",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    linhaAtual++;
                }

                if ((placa.isEmpty())|| marca.isEmpty() || modelo.isEmpty() || ano.isEmpty()) {
                    JOptionPane.showMessageDialog(painel,
                            "Por favor, preencha todos os campos obrigatórios (Placa, Marca, Modelo, Ano).",
                            "ERRO DE VALIDAÇÃO!",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int anoFabricacao = Integer.parseInt(ano);
                    if (anoFabricacao < 1950 || anoFabricacao > java.time.Year.now().getValue()) {
                        JOptionPane.showMessageDialog(painel,
                                "O Ano de Fabricação (" + ano + ") parece inválido.",
                                "Erro de Validação",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(painel,
                            "O Ano de Fabricação deve ser um número válido.",
                            "Erro de Formato",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String estadoTexto = "";
                if (botaoAtivo.isSelected()) {
                    estadoTexto = "ATIVO";
                } else if (botaoInativo.isSelected()) {
                    estadoTexto = "INATIVO";
                }

                StatusVeiculo statusVeiculo = StatusVeiculo.valueOf(estadoTexto);

                Veiculo novoVeiculo = new Veiculo(placa, marca, modelo, statusVeiculo, ano);
                VeiculoDAO veiculoDAO = new VeiculoDAO();
                veiculoDAO.SalvarVeiculo(novoVeiculo);
                JOptionPane.showMessageDialog(painel,
                        "Veículo Cadastrado!\n" +
                                "Placa: " + placa + "\n" +
                                "Marca: " + marca + "\n" +
                                "Modelo: " + modelo + "\n" +
                                "Ano: " + ano + "\n" +
                                "Estado: " + estadoTexto);

                veiculoController.AtualizarVeiculos(tableModelVeiculos, false, null);
            }
        });

        return painel;
    }

    private JPanel criarPainelListagemVeiculos() {

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        veiculoController.AtualizarVeiculos(tableModelVeiculos, false, null);

        JTable veiculosTable = new JTable(tableModelVeiculos);
        veiculosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);



        JScrollPane scrollPane = new JScrollPane(veiculosTable);
        painel.add(scrollPane,BorderLayout.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < veiculosTable.getColumnCount(); i++) {
            veiculosTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton botaoEditar = new JButton("Editar Veículos");
        JCheckBox checkBoxInativos = new JCheckBox("Apenas Inativos");
        JCheckBox checkBoxAtivos = new JCheckBox("Apenas Ativos");

        botaoEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int linhaEscolhida = (veiculosTable.getSelectedRow());
                    linhaEscolhida++;
                    if (linhaEscolhida == 0) {
                        JOptionPane.showMessageDialog(VeiculosView.this, "Nenhum veiculo selecionado!");
                        return;
                    }
                    JDialog janelaTemp = new JDialog(VeiculosView.this,"Editar Veículo", true);
                    janelaTemp.add(criarPainelEditarVeiculos(linhaEscolhida));
                    janelaTemp.pack();
                    janelaTemp.setLocationRelativeTo(VeiculosView.this);
                    janelaTemp.setVisible(true);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        checkBoxInativos.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(!checkBoxAtivos.isSelected()) {
                    boolean estadoCheckBox = checkBoxInativos.isSelected();
                    veiculoController.AtualizarVeiculos(tableModelVeiculos, estadoCheckBox, "INATIVO");
                } else{
                    JOptionPane.showMessageDialog(VeiculosView.this, "Filtro de ativos selecionado!");
                }
            }

        });

        checkBoxAtivos.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                    if(!checkBoxInativos.isSelected()) {
                        boolean estadoCheckBox = checkBoxAtivos.isSelected();
                        veiculoController.AtualizarVeiculos(tableModelVeiculos, estadoCheckBox, "ATIVO");
                    } else{
                        JOptionPane.showMessageDialog(VeiculosView.this, "Filtro de inativos selecionado!");
                    }
            }
        });


        painel.add(painelBotoes, BorderLayout.SOUTH);
        painelBotoes.add(botaoEditar,BorderLayout.SOUTH);
        painelBotoes.add(checkBoxInativos,BorderLayout.SOUTH);
        painelBotoes.add(checkBoxAtivos, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelEditarVeiculos(int linhaEscolhida){

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);

        JTextField campoPlaca = new JTextField(8);
        JTextField campoMarca = new JTextField(20);
        JTextField campoModelo = new JTextField(20);
        JTextField campoAnoFabricacao = new JTextField(4);
        JRadioButton botaoAtivo = new JRadioButton("Estado Veículo - ATIVO");
        JRadioButton botaoInativo = new JRadioButton("Estado Veículo - INATIVO");
        JButton botaoEditar = new JButton("Editar Veículo");

        botaoAtivo.setSelected(true);


        AbstractDocument docPlaca = (AbstractDocument) campoPlaca.getDocument();



        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("PLACA:"),gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(campoPlaca,gbc);



        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MARCA:"),gbc);


        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        painel.add(campoMarca,gbc);



        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MODELO:"),gbc);


        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        painel.add(campoModelo,gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ANO DE FABRICAÇÃO:"),gbc);


        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        painel.add(campoAnoFabricacao,gbc);


        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ESTADO DO VEÍCULO:"),gbc);


        ButtonGroup grupoEstadoVeiculo = new ButtonGroup();
        grupoEstadoVeiculo.add(botaoAtivo);
        grupoEstadoVeiculo.add(botaoInativo);


        JPanel painelEstadoVeiculo = new JPanel();
        painelEstadoVeiculo.add(botaoAtivo);
        painelEstadoVeiculo.add(new JLabel("\t|\t"));
        painelEstadoVeiculo.add(botaoInativo);


        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(painelEstadoVeiculo,gbc);


        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botaoEditar,gbc);


        campoPlaca.setText(veiculoDAO.LerVeiculos("placaVeiculo", linhaEscolhida));

        campoMarca.setText(veiculoDAO.LerVeiculos("marcaVeiculo", linhaEscolhida));

        campoModelo.setText(veiculoDAO.LerVeiculos("modeloVeiculo", linhaEscolhida));

        campoAnoFabricacao.setText(veiculoDAO.LerVeiculos("anoDeFabricacao", linhaEscolhida));

        if(Objects.equals(veiculoDAO.LerVeiculos("estadoVeiculo", linhaEscolhida), "ATIVO")){
            botaoAtivo.setSelected(true);
        } else{
            botaoInativo.setSelected(true);
        }


        botaoEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String placa = campoPlaca.getText();
                String marca = campoMarca.getText();
                String modelo = campoModelo.getText();
                String ano = campoAnoFabricacao.getText();

                String estadoTexto = "";

                if (botaoAtivo.isSelected()) {
                    estadoTexto = "ATIVO";
                } else if (botaoInativo.isSelected()) {
                    estadoTexto = "INATIVO";
                }

                StatusVeiculo statusVeiculo = StatusVeiculo.valueOf(estadoTexto);

                Veiculo veiculo = new Veiculo(placa, marca, modelo, statusVeiculo, ano);
                veiculo.setIdVeiculo(Long.parseLong(veiculoDAO.LerVeiculos("idVeiculo",linhaEscolhida)));
                veiculoDAO.EditarVeiculo(veiculo);
                veiculoController.AtualizarVeiculos(tableModelVeiculos, false, null);
                JOptionPane.showMessageDialog(painel,
                        "Veículo Alterado!\n" +
                                "Placa: " + placa + "\n" +
                                "Marca: " + marca + "\n" +
                                "Modelo: " + modelo + "\n" +
                                "Ano: " + ano + "\n" +
                                "Estado: " + estadoTexto);

                SwingUtilities.getWindowAncestor(painel).dispose();

            }
        });


        return painel;
    }


    public void refreshData() {
        veiculoController.AtualizarVeiculos(tableModelVeiculos, false, null);
    }
}
