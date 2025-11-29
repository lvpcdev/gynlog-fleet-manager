package view.main;

import view.gui.MenuView;
import view.gui.VeiculosView;

import javax.swing.*;

public class MainFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuView tela = new MenuView();
            tela.setVisible(true);
        });
    }
}