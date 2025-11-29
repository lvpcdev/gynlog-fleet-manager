package view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuView extends JFrame {

    public MenuView() {
        setTitle("MENU");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton botaoVeiculos = new JButton("Veículos");
        JButton botaoDespesas = new JButton("Despesas");

        Dimension botaoSize = new Dimension(200, 60);
        Font botaoFont = botaoVeiculos.getFont().deriveFont(Font.BOLD, 16f);

        botaoVeiculos.setPreferredSize(botaoSize);
        botaoVeiculos.setFont(botaoFont);

        botaoDespesas.setPreferredSize(botaoSize);
        botaoDespesas.setFont(botaoFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(botaoVeiculos, gbc);

        gbc.gridx = 1;
        painel.add(botaoDespesas, gbc);

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

        setVisible(true);
    }
}
