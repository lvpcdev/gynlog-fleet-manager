
import com.formdev.flatlaf.FlatDarkLaf;
import service.MovimentacaoService; // Importar MovimentacaoService
import view.gui.MenuView;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // NOVO: Inicializar quilometragem dos veículos existentes
        MovimentacaoService movimentacaoService = new MovimentacaoService();
        movimentacaoService.inicializarQuilometragemVeiculosExistentes();

        SwingUtilities.invokeLater(() -> {
            MenuView tela = new MenuView();
            tela.setVisible(true);
        });
    }
}
