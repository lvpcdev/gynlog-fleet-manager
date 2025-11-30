package view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuView extends JFrame {

    public MenuView() {
        setTitle("GynLog Fleet Manager - MENU PRINCIPAL");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JButton botaoVeiculos = new JButton("Gerenciar Veículos");
        JButton botaoDespesas = new JButton("Gerenciar Despesas");
        JButton botaoRelatorios = new JButton("Relatórios Gerenciais");


        Dimension botaoSize = new Dimension(220, 60);
        Font botaoFont = botaoVeiculos.getFont().deriveFont(Font.BOLD, 16f);

        botaoVeiculos.setPreferredSize(botaoSize);
        botaoVeiculos.setFont(botaoFont);

        botaoDespesas.setPreferredSize(botaoSize);
        botaoDespesas.setFont(botaoFont);

        botaoRelatorios.setPreferredSize(botaoSize);
        botaoRelatorios.setFont(botaoFont);


        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(botaoVeiculos, gbc);


        gbc.gridx = 1;
        painel.add(botaoDespesas, gbc);


        gbc.gridx = 2;
        painel.add(botaoRelatorios, gbc);


        add(painel, BorderLayout.CENTER);


        botaoVeiculos.addActionListener((ActionEvent e) -> {

            SwingUtilities.invokeLater(() -> {
                new VeiculosView();
                MenuView.this.dispose();
            });
        });


        botaoDespesas.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                new DespesasView();
                MenuView.this.dispose();
            });
        });


        botaoRelatorios.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                new RelatoriosView();
                MenuView.this.dispose();
            });
        });


        setVisible(true);
    }
}
