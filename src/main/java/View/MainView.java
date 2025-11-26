package View;

import javax.swing.*;

public class MainView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CadastroVeiculosView tela = new CadastroVeiculosView();
            tela.setVisible(true);
        });

    }
}