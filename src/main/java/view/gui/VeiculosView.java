package view.gui;


import persistance.dao.VeiculoDAO;
import model.entities.Veiculo;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class VeiculosView extends JFrame {

    public VeiculosView() {
        setTitle("VEÍCULOS");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abasPrincipais = new JTabbedPane();
        this.add(abasPrincipais, BorderLayout.CENTER);

        //=====CADASTRO=======
        JPanel painelCadastro = criarPainelCadastroVeiculos();
        abasPrincipais.addTab("Cadastro de Veículos",painelCadastro);
        setVisible(true);


        //====LISTAGEM========
        JPanel painelListagem = criarPainelListagemVeiculos();
        abasPrincipais.addTab("LISTAGEM", painelListagem);
        setVisible(true);

        //====ATUALIZA=======



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

        //FILTROS
        AbstractDocument docPlaca = (AbstractDocument) campoPlaca.getDocument();



        //======PLACA=======

        //RÓTULO
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("PLACA:"),gbc);

        //LINHA
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(campoPlaca,gbc);


         //======MARCA=======

        //RÓTULo
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MARCA:"),gbc);

        //LINHA
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        painel.add(campoMarca,gbc);


        //======MODELO=======

        //RÓTULo
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        painel.add(new JLabel("MODELO:"),gbc);

        //LINHA
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        painel.add(campoModelo,gbc);

        //======ANO DE FABRICACAO=======

        //RÓTULo
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ANO DE FABRICAÇÃO:"),gbc);

        //LINHA
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        painel.add(campoAnoFabricacao,gbc);

        //=====ESTADO=============

        //RÓTULO
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        painel.add(new JLabel("ESTADO DO VEÍCULO:"),gbc);

        //GRUPO BOTÕES DE ESTADO
        ButtonGroup grupoEstadoVeiculo = new ButtonGroup();
        grupoEstadoVeiculo.add(botaoAtivo);
        grupoEstadoVeiculo.add(botaoInativo);

        //PAINEL DOS BOTÕES
        JPanel painelEstadoVeiculo = new JPanel();
        painelEstadoVeiculo.add(botaoAtivo);
        painelEstadoVeiculo.add(new JLabel("\t|\t"));
        painelEstadoVeiculo.add(botaoInativo);

        //LINHA
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(painelEstadoVeiculo,gbc);

        //====BOTÃO CADASTRAR========
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

                boolean estado = false;
                String estadoTexto = "";

                if (botaoAtivo.isSelected()) {
                    estadoTexto = "ATIVO";
                    estado = true;
                } else if (botaoInativo.isSelected()) {
                    estadoTexto = "INATIVO";
                    estado = false;
                }

                Veiculo novoVeiculo = new Veiculo(placa, marca, modelo, estado, ano);
                VeiculoDAO veiculoDAO = new VeiculoDAO();
                veiculoDAO.SalvarVeiculo(novoVeiculo);
                JOptionPane.showMessageDialog(painel,
                        "Veículo Cadastrado!\n" +
                                "Placa: " + placa + "\n" +
                                "Marca: " + marca + "\n" +
                                "Modelo: " + modelo + "\n" +
                                "Ano: " + ano + "\n" +
                                "Estado: " + estadoTexto);
            }
        });


                return painel;
    }

    private JPanel criarPainelListagemVeiculos(){

        JPanel painel = new JPanel(new BorderLayout(10,10));
        painel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String[] colunas = {"ID","Placa","Marca","Modelo","Ano Fabricação","Estado"};

        DefaultTableModel tableModel = new DefaultTableModel(colunas,0);

        tableModel.addRow(new Object[] {1,"SCU7H31","Fiat","Polo",2020,"Ativo"});//MOCk para teste


        JTable veiculosTable = new JTable(tableModel);
        veiculosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Deixar em negrito o cabeçalho
        veiculosTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,column);

            label.setHorizontalAlignment(CENTER);
            Font originalFont = label.getFont();
            label.setFont(originalFont.deriveFont(Font.BOLD));
            return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(veiculosTable);
        painel.add(scrollPane,BorderLayout.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        veiculosTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);



        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.add(new JButton("Editar Selecionado"));
        painelBotoes.add(new JButton("Excluir Selecionado"));

        painel.add(painelBotoes, BorderLayout.SOUTH);
        return painel;
    }


}
