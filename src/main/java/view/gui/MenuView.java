package view.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuView extends JFrame {

    public MenuView() {
        setTitle("GynLog Fleet Manager - MENU PRINCIPAL");
        setSize(1100, 700);
        setExtendedState(JFrame.NORMAL);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(45, 52, 54));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        ImageIcon logoOriginal = new ImageIcon(getClass().getClassLoader().getResource("resources/LogoGynLogFleetManager.png"));
        Image logoRedimensionada = logoOriginal.getImage().getScaledInstance(150, 80, Image.SCALE_SMOOTH);
        JLabel labelLogo = new JLabel(new ImageIcon(logoRedimensionada));
        labelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        sidebar.add(labelLogo);
        sidebar.setPreferredSize(new Dimension(180, 0));
        final Dimension originalSidebarSize = sidebar.getPreferredSize();
        final int collapsedWidth = 48;

        Dimension botaoSize = new Dimension(160, 40);
        Font botaoFont = new Font("SansSerif", Font.BOLD, 14);

        JButton botaoVeiculos = new JButton("Veículos");
        JButton botaoDespesas = new JButton("Despesas");
        JButton botaoRelatorios = new JButton("Relatórios");


        JButton[] botoes = {botaoVeiculos, botaoDespesas, botaoRelatorios};
        for (JButton b : botoes) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, botaoSize.height));
            b.setPreferredSize(botaoSize);
            b.setFont(botaoFont);
            b.setFocusPainted(false);
            b.setBackground(new Color(68, 71, 90));
            b.setForeground(Color.WHITE);
            b.setOpaque(true);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            sidebar.add(b);
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }


        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        VeiculosView veiculosView = new VeiculosView(false);
        DespesasView despesasView = new DespesasView(false);
        RelatoriosView relatoriosView = new RelatoriosView(false);

        JPanel veiculosPanel = veiculosView.getMainPanel();
        JPanel despesasPanel = despesasView.getMainPanel();
        JPanel relatoriosPanel = relatoriosView.getMainPanel();


        contentPanel.add(veiculosPanel, "VEICULOS");
        contentPanel.add(despesasPanel, "DESPESAS");
        contentPanel.add(relatoriosPanel, "RELATORIOS");


        Color selectedBg = new Color(0, 123, 255);
        Color defaultBg = new Color(68, 71, 90);

        Runnable selectVeiculos = () -> {
            CardLayout cl = (CardLayout) (contentPanel.getLayout());
            cl.show(contentPanel, "VEICULOS");
            botaoVeiculos.setBackground(selectedBg);
            botaoDespesas.setBackground(defaultBg);
            botaoRelatorios.setBackground(defaultBg);
        };
        Runnable selectDespesas = () -> {
            CardLayout cl = (CardLayout) (contentPanel.getLayout());
            cl.show(contentPanel, "DESPESAS");
            botaoVeiculos.setBackground(defaultBg);
            botaoDespesas.setBackground(selectedBg);
            botaoRelatorios.setBackground(defaultBg);
        };
        Runnable selectRelatorios = () -> {
            CardLayout cl = (CardLayout) (contentPanel.getLayout());
            cl.show(contentPanel, "RELATORIOS");
            botaoVeiculos.setBackground(defaultBg);
            botaoDespesas.setBackground(defaultBg);
            botaoRelatorios.setBackground(selectedBg);
        };


        botaoVeiculos.addActionListener((ActionEvent e) -> {

            veiculosView.refreshData();
            despesasView.refreshData();
           relatoriosView.refreshData();
            selectVeiculos.run();
        });
        botaoDespesas.addActionListener((ActionEvent e) -> {
            veiculosView.refreshData();
            despesasView.refreshData();
            relatoriosView.refreshData();
            selectDespesas.run();
        });
        botaoRelatorios.addActionListener((ActionEvent e) -> {
            veiculosView.refreshData();
            despesasView.refreshData();
            relatoriosView.refreshData();
            selectRelatorios.run();
        });


        JPanel togglePanel = new JPanel();
        togglePanel.setLayout(new BoxLayout(togglePanel, BoxLayout.Y_AXIS));
        togglePanel.setOpaque(false);
        togglePanel.add(Box.createVerticalGlue());

        JButton btnCollapse = new JButton("◀");
        JButton btnExpand = new JButton("▶");
        btnCollapse.setMaximumSize(new Dimension(36, 36));
        btnExpand.setMaximumSize(new Dimension(36, 36));
        btnCollapse.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExpand.setAlignmentX(Component.CENTER_ALIGNMENT);



        JPanel collapsedPanel = new JPanel(new GridBagLayout());
        collapsedPanel.setOpaque(false);
        collapsedPanel.setPreferredSize(new Dimension(collapsedWidth, 0));
        collapsedPanel.add(btnExpand);


        JPanel westContainer = new JPanel(new CardLayout());
        westContainer.add(sidebar, "SIDEBAR");
        westContainer.add(collapsedPanel, "COLLAPSED");


        final boolean[] animating = {false};

        btnCollapse.addActionListener((ActionEvent e) -> {
            if (animating[0]) return;
            animating[0] = true;
            btnCollapse.setEnabled(false);
            btnExpand.setEnabled(false);

            int from = sidebar.getPreferredSize().width;
            int step = Math.max(8, (from - collapsedWidth) / 10);

            Timer timer = new Timer(12, null);
            final int[] current = {from};
            timer.addActionListener(evt -> {
                current[0] = Math.max(collapsedWidth, current[0] - step);
                sidebar.setPreferredSize(new Dimension(current[0], 0));
                sidebar.revalidate();
                sidebar.repaint();
                mainPanel.revalidate();
                westContainer.revalidate();
                westContainer.repaint();
                if (current[0] <= collapsedWidth) {
                    timer.stop();

                    sidebar.setPreferredSize(new Dimension(collapsedWidth, 0));
                    sidebar.revalidate();
                    sidebar.repaint();
                    CardLayout cl = (CardLayout) (westContainer.getLayout());
                    cl.show(westContainer, "COLLAPSED");

                    sidebar.setVisible(false);
                    animating[0] = false;
                    btnExpand.setEnabled(true);
                }
            });
            timer.start();
        });

        btnExpand.addActionListener((ActionEvent e) -> {
            if (animating[0]) return;
            animating[0] = true;
            btnCollapse.setEnabled(false);
            btnExpand.setEnabled(false);


            sidebar.setPreferredSize(new Dimension(collapsedWidth, 0));
            sidebar.setVisible(true);
            CardLayout clBefore = (CardLayout) (westContainer.getLayout());
            clBefore.show(westContainer, "SIDEBAR");
            westContainer.revalidate();
            westContainer.repaint();

            int step = Math.max(8, (originalSidebarSize.width - collapsedWidth) / 10);

            Timer timer = new Timer(12, null);
            final int[] current = {collapsedWidth};
            timer.addActionListener(evt -> {
                current[0] = Math.min(originalSidebarSize.width, current[0] + step);
                sidebar.setPreferredSize(new Dimension(current[0], 0));
                sidebar.revalidate();
                sidebar.repaint();
                mainPanel.revalidate();
                westContainer.revalidate();
                westContainer.repaint();
                if (current[0] >= originalSidebarSize.width) {
                    timer.stop();

                    CardLayout cl = (CardLayout) (westContainer.getLayout());
                    cl.show(westContainer, "SIDEBAR");
                    sidebar.setPreferredSize(new Dimension(originalSidebarSize.width, 0));
                    sidebar.setVisible(true);
                    westContainer.revalidate();
                    westContainer.repaint();
                    animating[0] = false;
                    btnCollapse.setEnabled(true);
                }
            });
            timer.start();
        });

        togglePanel.add(btnCollapse);
        togglePanel.add(Box.createRigidArea(new Dimension(0, 8)));

        togglePanel.add(Box.createVerticalStrut(12));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(togglePanel);


        mainPanel.add(westContainer, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);


        add(mainPanel);


        setVisible(true);
    }
}
