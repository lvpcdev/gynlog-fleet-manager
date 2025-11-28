package View;

import javax.swing.*;

public class MainView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VeiculosView tela = new VeiculosView();
            tela.setVisible(true);
        });
    }
}