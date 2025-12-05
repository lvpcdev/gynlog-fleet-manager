package view.main;

import com.formdev.flatlaf.FlatDarkLaf;
import view.gui.MenuView;
import view.gui.VeiculosView;

import javax.swing.*;

public class MainFrame {
    public static void main(String[] args) {

        try {
//           UIManager.setLookAndFeel(new FlatLightLaf());
             UIManager.setLookAndFeel(new FlatDarkLaf());


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            MenuView tela = new MenuView();
            tela.setVisible(true);
        });
    }
}