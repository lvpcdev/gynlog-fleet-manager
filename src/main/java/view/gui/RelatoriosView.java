package view.gui;

import controller.MovimentacaoController;
import controller.VeiculoController;
import model.entities.Movimentacao;
import model.entities.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class RelatoriosView extends JFrame {

    private final MovimentacaoController movimentacaoController;
    private final VeiculoController veiculoController;

    private JComboBox<Veiculo> comboVeiculos;
    private JComboBox<Month> comboMes;
    private JComboBox<Integer> comboAno;
    private JComboBox<String> comboCategoria;
    private JTextArea areaResultados;

    public RelatoriosView() {
        this.movimentacaoController = new MovimentacaoController();
        this.veiculoController = new VeiculoController();

        setTitle("RELATÓRIOS GERENCIAIS");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setContentPane(buildMainPanel(true));
        setVisible(true);
    }

    RelatoriosView(boolean forEmbed) {
        this.movimentacaoController = new MovimentacaoController();
        this.veiculoController = new VeiculoController();
    }

    public static JPanel createMainPanel() {
        RelatoriosView r = new RelatoriosView(false);
        return r.buildMainPanel(false);
    }

    public JPanel getMainPanel() {
        return buildMainPanel(false);
    }

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

            botaoVoltar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new MenuView();
                            RelatoriosView.this.dispose();
                        }
                    });
                }
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

        comboVeiculos = new JComboBox<Veiculo>();
        comboVeiculos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Veiculo) {
                    Veiculo v = (Veiculo) value;
                    setText(v.getPlaca() + " (" + v.getModelo() + ")");
                } else if (value == null) {
                    setText("Todos os Veículos");
                }
                return this;
            }
        });

        comboMes = new JComboBox<Month>(Month.values());
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

        comboAno = new JComboBox<Integer>();

        comboCategoria = new JComboBox<>();
        try {
            List<String> categorias = veiculoController.listarCategorias();
            for (String cat : categorias){
                comboCategoria.addItem(cat);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: "+ e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        painel.add(new JLabel("Veículo:"));
        painel.add(comboVeiculos);
        painel.add(new JLabel("Mês:"));
        painel.add(comboMes);
        painel.add(new JLabel("Ano:"));
        painel.add(comboAno);
        painel.add(new JLabel("Categoria"));
        painel.add(comboCategoria);


        return painel;
    }

    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(10, 1, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Gerar Relatório"));

        JButton btnDespesasVeiculo = new JButton("1. Despesas por Veículo");
        JButton btnSomaGeralMes = new JButton("2. Somatório Geral do Mês");
        JButton btnSomaCombustivelMes = new JButton("3. Total Combustível no Mês");
        JButton btnSomaIpvaAno = new JButton("4. Somatório IPVA no Ano");
        JButton btnListarInativos = new JButton("5. Listar Veículos Inativos");
        JButton btnMultasAno = new JButton("6. Multas por Veículo no Ano");
        JButton btnMediaDespesaVeiculoCategoria = new JButton("7. Média das Despesas por Categoria de Veículo");
        JButton btnConsumoMedioVeiculo = new JButton("8. Consumo Médio por Veículo");
        JButton btnCosumoMedioIPVA = new JButton("9. Custo Médio do IPVA em Determinado Ano");
        JButton btnIndentificarVeiculoMaiorMenorCusto = new JButton("10. Identificar o Veículo com Maior e Menor Custo de Consumo");

        btnDespesasVeiculo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
                if (veiculo == null) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Por favor, selecione um veículo.", "Filtro Necessário", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    List<Movimentacao> lista = movimentacaoController.listarPorVeiculo(veiculo.getId());
                    BigDecimal total = movimentacaoController.totalPorVeiculo(veiculo.getId());
                    areaResultados.setText(montarRelatorioMovimentacoes("DESPESAS DO VEÍCULO: " + veiculo.getPlaca(), lista, total));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSomaGeralMes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Month mes = (Month) comboMes.getSelectedItem();
                    int ano = (int) comboAno.getSelectedItem();
                    YearMonth mesAno = YearMonth.of(ano, mes);
                    List<Movimentacao> lista = movimentacaoController.listarPorMes(mesAno);
                    BigDecimal total = movimentacaoController.totalPorMes(mesAno);
                    areaResultados.setText(montarRelatorioMovimentacoes("SOMATÓRIO GERAL - " + mes.getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toUpperCase() + "/" + ano, lista, total));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSomaCombustivelMes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Month mes = (Month) comboMes.getSelectedItem();
                    int ano = (int) comboAno.getSelectedItem();
                    YearMonth mesAno = YearMonth.of(ano, mes);
                    List<Movimentacao> lista = movimentacaoController.listarCombustivelPorMes(mesAno);
                    BigDecimal total = movimentacaoController.totalCombustivelPorMes(mesAno);
                    areaResultados.setText(montarRelatorioMovimentacoes("COMBUSTÍVEL - " + mes.getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toUpperCase() + "/" + ano, lista, total));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSomaIpvaAno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ano = (int) comboAno.getSelectedItem();
                    Year anoFiltro = Year.of(ano);
                    List<Movimentacao> lista = movimentacaoController.listarIpvaPorAno(anoFiltro);
                    BigDecimal total = movimentacaoController.totalIpvaPorAno(anoFiltro);
                    areaResultados.setText(montarRelatorioMovimentacoes("IPVA - ANO " + ano, lista, total));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnListarInativos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Veiculo> inativos = veiculoController.listarInativos();
                    areaResultados.setText(montarRelatorioVeiculos("VEÍCULOS INATIVOS", inativos));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnMultasAno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
                if (veiculo == null) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Por favor, selecione um veículo.", "Filtro Necessário", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    int ano = (int) comboAno.getSelectedItem();
                    Year anoFiltro = Year.of(ano);
                    List<Movimentacao> lista = movimentacaoController.listarMultasPorVeiculo(veiculo.getId(), anoFiltro);
                    BigDecimal total = movimentacaoController.totalMultasPorVeiculo(veiculo.getId(), anoFiltro);
                    areaResultados.setText(montarRelatorioMovimentacoes("MULTAS - " + veiculo.getPlaca() + " - " + ano, lista, total));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnMediaDespesaVeiculoCategoria.addActionListener(e -> {
            String categoria = (String) comboCategoria.getSelectedItem();
            if(categoria == null || categoria.isBlank()){
                JOptionPane.showMessageDialog(RelatoriosView.this,
                        "Por favor, selecione uma categoria","Filtro Necessario",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try{
                BigDecimal media = movimentacaoController.mediaDespesasPorCategoria(categoria);
                String resultado = "========================================\n" +
                                "  MÉDIA DE DESPESAS - CATEGORIA: " + categoria.toUpperCase() + "\n" +
                                "========================================\n\n" +
                                "  Média das despesas: R$ " + media + "\n\n" +
                                "========================================\n";
                areaResultados.setText(resultado);
            }catch (Exception ex){
                JOptionPane.showMessageDialog(RelatoriosView.this,
                        "Erro: "+ ex.getMessage(),"Erro",JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCosumoMedioIPVA.addActionListener(e -> {
            Integer anoSelecionado = (Integer) comboAno.getSelectedItem();

            if(anoSelecionado == null){
                JOptionPane.showMessageDialog(RelatoriosView.this,
                        "Por favor, selecione um ano.", "Filtro Necessário",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Year ano = Year.of(anoSelecionado);
                BigDecimal media = movimentacaoController.mediaIpvaPorAno(ano);
                String resultado = "========================================\n" +
                        "  CUSTO MÉDIO DO IPVA - ANO: " + anoSelecionado + "\n" +
                        "========================================\n\n" +
                        "  Média do IPVA no ano: R$ " + media + "\n\n" +
                        "========================================\n";
                areaResultados.setText(resultado);
            }   catch (Exception ex){
                JOptionPane.showMessageDialog(RelatoriosView.this,
                        "Erro: "+ ex.getMessage(),"Erro",JOptionPane.ERROR_MESSAGE);
            }
        });

        painel.add(btnDespesasVeiculo);
        painel.add(btnSomaGeralMes);
        painel.add(btnSomaCombustivelMes);
        painel.add(btnSomaIpvaAno);
        painel.add(btnListarInativos);
        painel.add(btnMultasAno);
        painel.add(btnMediaDespesaVeiculoCategoria);
        painel.add(btnConsumoMedioVeiculo);
        painel.add(btnCosumoMedioIPVA);
        painel.add(btnIndentificarVeiculoMaiorMenorCusto);

        return painel;
    }

    private String montarRelatorioMovimentacoes(String titulo, List<Movimentacao> lista, BigDecimal total) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append(titulo).append("\n");
        sb.append("========================================\n\n");

        if (lista.isEmpty()) {
            sb.append("Nenhum registro encontrado.\n");
        } else {
            for (Movimentacao mov : lista) {
                sb.append("Data: ").append(mov.getData().format(fmt)).append("\n");
                sb.append("Veículo: ").append(mov.getVeiculo().getPlaca()).append("\n");
                sb.append("Tipo: ").append(mov.getTipoDespesa().getDescricao()).append("\n");
                sb.append("Descrição: ").append(mov.getDescricao()).append("\n");
                sb.append("Valor: ").append(moeda.format(mov.getValor())).append("\n");
                sb.append("----------------------------------------\n");
            }
            sb.append("\nTOTAL: ").append(moeda.format(total)).append("\n");
        }

        return sb.toString();
    }

    private String montarRelatorioVeiculos(String titulo, List<Veiculo> lista) {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append(titulo).append("\n");
        sb.append("========================================\n\n");

        if (lista.isEmpty()) {
            sb.append("Nenhum veículo encontrado.\n");
        } else {
            for (Veiculo v : lista) {
                sb.append("Placa: ").append(v.getPlaca()).append("\n");
                sb.append("Marca: ").append(v.getMarca()).append("\n");
                sb.append("Modelo: ").append(v.getModelo()).append("\n");
                sb.append("Ano: ").append(v.getAnoDeFabricacao()).append("\n");
                sb.append("Status: ").append(v.getStatusVeiculo()).append("\n");
                sb.append("----------------------------------------\n");
            }
            sb.append("\nTotal de veículos inativos: ").append(lista.size()).append("\n");
        }

        return sb.toString();
    }

    private void carregarFiltros() {
        comboVeiculos.removeAllItems();
        comboAno.removeAllItems();
        comboCategoria.removeAllItems();

        comboVeiculos.addItem(null);
        List<Veiculo> veiculos = veiculoController.listarTodos();
        for (Veiculo v : veiculos) {
            comboVeiculos.addItem(v);
        }

        int anoAtual = Year.now().getValue();
        for (int i = anoAtual; i >= anoAtual - 10; i--) {
            comboAno.addItem(i);
        }

        comboCategoria.addItem(null);
        List<String> categorias = veiculoController.listarCategorias();
        for (String cat : categorias){
            comboCategoria.addItem(cat);
        }

        comboMes.setSelectedItem(Month.from(java.time.LocalDate.now()));
        comboAno.setSelectedItem(anoAtual);
    }
}