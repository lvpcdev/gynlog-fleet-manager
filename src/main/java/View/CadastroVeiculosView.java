package View;

import javax.swing.*;
import java.awt.*;



public class CadastroVeiculosView extends JFrame {

    public CadastroVeiculosView() {
        setTitle("Cadastro de Veículos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelCadastro = criarPainelCadastroVeiculos();

        JTabbedPane abasPrincipais = new JTabbedPane();
        abasPrincipais.addTab("Cadastro de Veículos",painelCadastro);
        this.add(abasPrincipais, BorderLayout.CENTER);

        setVisible(true);

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


        return painel;
    }




}
