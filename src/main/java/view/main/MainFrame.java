package view.main;

import view.gui.CadastroVeiculosView;

import javax.swing.*;

public class MainFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CadastroVeiculosView tela = new CadastroVeiculosView();
            tela.setVisible(true);
        });
    }
}