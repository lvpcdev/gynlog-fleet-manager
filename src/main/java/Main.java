
import View.CadastroVeiculosView;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CadastroVeiculosView tela = new CadastroVeiculosView();
            tela.setVisible(true);
        });

    }

}
