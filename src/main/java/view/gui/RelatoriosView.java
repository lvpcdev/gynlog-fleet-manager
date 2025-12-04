package view.gui;

import controller.RelatorioController;
import model.entities.Veiculo;
import persistence.dao.VeiculoDAO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class RelatoriosView extends JFrame {

    private final RelatorioController relatorioController;
    private JComboBox<Veiculo> comboVeiculos;
    private JComboBox<Month> comboMes;
    private JComboBox<Integer> comboAno;
    private JTextArea areaResultados;

    public RelatoriosView() {
        this.relatorioController = new RelatorioController();

        setTitle("RELATÓRIOS GERENCIAIS");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        this.setContentPane(buildMainPanel(true));
        setVisible(true);
    }

    // package-private constructor for embed usage
    RelatoriosView(boolean forEmbed) {
        this.relatorioController = new RelatorioController();
    }

    public static JPanel createMainPanel() {
        RelatoriosView r = new RelatoriosView(false);
        return r.buildMainPanel(false);
    }

    public JPanel getMainPanel() {
        return buildMainPanel(false);
    }

    // Public method to refresh filters/data when embedded
    public void refreshData() {
        carregarFiltros();
    }

    private JPanel buildMainPanel(boolean includeTopBackButton) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (includeTopBackButton) {
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton botaoVoltar = new JButton("Voltar ao Menu");
            topo.add(botaoVoltar);
            root.add(topo, BorderLayout.NORTH);

            botaoVoltar.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new MenuView();
                    RelatoriosView.this.dispose();
                });
            });
        }

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        root.add(painelPrincipal, BorderLayout.CENTER);

        painelPrincipal.add(criarPainelFiltros(), BorderLayout.NORTH);

        areaResultados = new JTextArea("Selecione os filtros e gere um relatório.");
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 14));
        painelPrincipal.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        painelPrincipal.add(criarPainelAcoes(), BorderLayout.WEST);

        carregarFiltros();

        return root;
    }

    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createTitledBorder("Filtros"));

        comboVeiculos = new JComboBox<>();
        comboVeiculos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Veiculo) {
                    setText(((Veiculo) value).getPlaca() + " (" + ((Veiculo) value).getModelo() + ")");
                } else if (value == null) {
                    setText("Todos os Veículos");
                }
                return this;
            }
        });

        comboMes = new JComboBox<>(Month.values());
        comboMes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Month) {
                    Month mes = (Month) value;
                    Locale ptBR = new Locale("pt", "BR");
                    String nomeMes = mes.getDisplayName(TextStyle.FULL, ptBR);
                    String nomeFormatado = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1).toLowerCase();
                    setText(nomeFormatado);
                }
                return this;
            }
        });

        comboAno = new JComboBox<>();

        painel.add(new JLabel("Veículo:"));
        painel.add(comboVeiculos);
        painel.add(new JLabel("Mês:"));
        painel.add(comboMes);
        painel.add(new JLabel("Ano:"));
        painel.add(comboAno);

        return painel;
    }

    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(6, 1, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Gerar Relatório"));

        JButton btnDespesasVeiculo = new JButton("1. Despesas por Veículo");
        JButton btnSomaGeralMes = new JButton("2. Somatório Geral do Mês");
        JButton btnSomaCombustivelMes = new JButton("3. Total Combustível no Mês");
        JButton btnSomaIpvaAno = new JButton("4. Somatório IPVA no Ano");
        JButton btnListarInativos = new JButton("5. Listar Veículos Inativos");
        JButton btnMultasAno = new JButton("6. Multas por Veículo no Ano");

        btnDespesasVeiculo.addActionListener(e -> {
            Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
            if (veiculo == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um veículo.", "Filtro Necessário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String relatorio = relatorioController.gerarRelatorioDespesasPorVeiculo(veiculo.getIdVeiculo());
            areaResultados.setText(relatorio);
        });

        btnSomaGeralMes.addActionListener(e -> {
            Month mes = (Month) comboMes.getSelectedItem();
            int ano = (int) comboAno.getSelectedItem();

            String relatorioCompleto = relatorioController.gerarRelatorioSomaGeralMes(mes.getValue(), ano);


            areaResultados.setText(relatorioCompleto);
        });

        btnSomaCombustivelMes.addActionListener(e -> {
            Month mes = (Month) comboMes.getSelectedItem();
            int ano = (int) comboAno.getSelectedItem();


            String relatorioCompleto = relatorioController.gerarRelatorioSomaCombustivelMes(mes.getValue(), ano);


            areaResultados.setText(relatorioCompleto);
        });

        btnSomaIpvaAno.addActionListener(e -> {
            int ano = (int) comboAno.getSelectedItem();
            BigDecimal total = relatorioController.calcularTotalPorTipoDespesaNoAno("IPVA", ano);

            String resultado = String.format("Relatório: Somatório de IPVA da Frota\n" +
                            "--------------------------------------\n" +
                            "Ano: %d\n" +
                            "Total IPVA: %s",
                    ano, formatarMoeda(total));
            areaResultados.setText(resultado);
        });

        btnListarInativos.addActionListener(e -> {
            String relatorio = relatorioController.gerarRelatorioVeiculosInativos();
            areaResultados.setText(relatorio);
        });

        btnMultasAno.addActionListener(e -> {
            Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
            int ano = (int) comboAno.getSelectedItem();
            if (veiculo == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um veículo.", "Filtro Necessário", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String relatorio = relatorioController.gerarRelatorioMultasPorVeiculoAno(veiculo.getIdVeiculo(), ano);
            areaResultados.setText(relatorio);
        });

        painel.add(btnDespesasVeiculo);
        painel.add(btnSomaGeralMes);
        painel.add(btnSomaCombustivelMes);
        painel.add(btnSomaIpvaAno);
        painel.add(btnListarInativos);
        painel.add(btnMultasAno);

        return painel;
    }

    private void carregarFiltros() {
        // limpar antes de repovoar para evitar duplicatas
        comboVeiculos.removeAllItems();
        comboAno.removeAllItems();

        // adicionar uma opção "Todos" (null) seguida dos veículos atuais
        comboVeiculos.addItem(null);
        List<Veiculo> veiculos = new VeiculoDAO().listarTodos();
        veiculos.forEach(comboVeiculos::addItem);

        int anoAtual = Year.now().getValue();
        for (int i = anoAtual; i >= anoAtual - 10; i--) {
            comboAno.addItem(i);
        }

        comboMes.setSelectedItem(Month.from(java.time.LocalDate.now()));
        comboAno.setSelectedItem(anoAtual);
    }

    private String formatarMoeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }
}
