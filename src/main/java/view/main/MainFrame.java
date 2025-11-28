package view.main;

import view.gui.VeiculosView;

import javax.swing.*;

public class MainFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VeiculosView tela = new VeiculosView();
            tela.setVisible(true);
        });
    }
}