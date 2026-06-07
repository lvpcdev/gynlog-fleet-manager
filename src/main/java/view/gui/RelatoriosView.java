package view.gui;

import controller.MovimentacaoController;
import controller.VeiculoController;
import model.entities.Movimentacao;
import model.entities.Veiculo;
import service.MovimentacaoService;
import util.CsvExporter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
    private JTextArea areaResultados;
    
    // ADICIONAR EM RelatoriosView
    private JButton btnExportarCsv;
    private List<MovimentacaoService.VeiculoCusto> dadosUltimoRelatorio10;

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
        // ADICIONAR EM RelatoriosView: Alterado GridLayout para 8 linhas para incluir botão exportar
        painel.setLayout(new GridLayout(8, 1, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Gerar Relatório"));

        JButton btnDespesasVeiculo = new JButton("1. Despesas por Veículo");
        JButton btnSomaGeralMes = new JButton("2. Somatório Geral do Mês");
        JButton btnSomaCombustivelMes = new JButton("3. Total Combustível no Mês");
        JButton btnSomaIpvaAno = new JButton("4. Somatório IPVA no Ano");
        JButton btnListarInativos = new JButton("5. Listar Veículos Inativos");
        JButton btnMultasAno = new JButton("6. Multas por Veículo no Ano");
        JButton btnMaiorMenorCusto = new JButton("10. Maior e Menor Custo");
        
        // ADICIONAR EM RelatoriosView
        btnExportarCsv = new JButton("Exportar para CSV");
        btnExportarCsv.setEnabled(false);
        btnExportarCsv.setBackground(new Color(200, 230, 201)); // Verde suave para destaque

        btnDespesasVeiculo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnExportarCsv.setEnabled(false);
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
                btnExportarCsv.setEnabled(false);
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
                btnExportarCsv.setEnabled(false);
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
                btnExportarCsv.setEnabled(false);
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
                btnExportarCsv.setEnabled(false);
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
                btnExportarCsv.setEnabled(false);
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

        btnMaiorMenorCusto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dadosUltimoRelatorio10 = movimentacaoController.obterMaiorMenorCusto();
                    areaResultados.setText(montarRelatorioMaiorMenorCusto(dadosUltimoRelatorio10));
                    btnExportarCsv.setEnabled(!dadosUltimoRelatorio10.isEmpty());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RelatoriosView.this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ADICIONAR EM RelatoriosView
        btnExportarCsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salvar Relatório CSV");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos CSV (*.csv)", "csv"));
                
                int userSelection = fileChooser.showSaveDialog(RelatoriosView.this);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();
                    if (!filePath.toLowerCase().endsWith(".csv")) {
                        filePath += ".csv";
                    }
                    
                    try {
                        CsvExporter.exportarRelatorio10(dadosUltimoRelatorio10, filePath);
                        JOptionPane.showMessageDialog(RelatoriosView.this, "Relatório exportado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RelatoriosView.this, "Erro ao exportar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        painel.add(btnDespesasVeiculo);
        painel.add(btnSomaGeralMes);
        painel.add(btnSomaCombustivelMes);
        painel.add(btnSomaIpvaAno);
        painel.add(btnListarInativos);
        painel.add(btnMultasAno);
        painel.add(btnMaiorMenorCusto);
        // ADICIONAR EM RelatoriosView
        painel.add(btnExportarCsv);

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

    private String montarRelatorioMaiorMenorCusto(List<MovimentacaoService.VeiculoCusto> lista) {
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("RELATÓRIO #10 - MAIOR E MENOR CUSTO\n");
        sb.append("========================================\n\n");

        if (lista.isEmpty()) {
            sb.append("Nenhum dado disponível.\n");
        } else {
            MovimentacaoService.VeiculoCusto menor = lista.get(0);
            sb.append(">>> VEÍCULO COM MENOR CUSTO <<<\n");
            sb.append("Placa: ").append(menor.getVeiculo().getPlaca()).append("\n");
            sb.append("Marca: ").append(menor.getVeiculo().getMarca()).append("\n");
            sb.append("Modelo: ").append(menor.getVeiculo().getModelo()).append("\n");
            sb.append("Custo Total: ").append(moeda.format(menor.getCustoTotal())).append("\n");
            sb.append("----------------------------------------\n\n");

            if (lista.size() > 1) {
                MovimentacaoService.VeiculoCusto maior = lista.get(lista.size() - 1);
                sb.append(">>> VEÍCULO COM MAIOR CUSTO <<<\n");
                sb.append("Placa: ").append(maior.getVeiculo().getPlaca()).append("\n");
                sb.append("Marca: ").append(maior.getVeiculo().getMarca()).append("\n");
                sb.append("Modelo: ").append(maior.getVeiculo().getModelo()).append("\n");
                sb.append("Custo Total: ").append(moeda.format(maior.getCustoTotal())).append("\n");
                sb.append("----------------------------------------\n");
            }
        }

        return sb.toString();
    }

    private void carregarFiltros() {
        comboVeiculos.removeAllItems();
        comboAno.removeAllItems();

        comboVeiculos.addItem(null);
        List<Veiculo> veiculos = veiculoController.listarTodos();
        for (Veiculo v : veiculos) {
            comboVeiculos.addItem(v);
        }

        int anoAtual = Year.now().getValue();
        for (int i = anoAtual; i >= anoAtual - 10; i--) {
            comboAno.addItem(i);
        }

        comboMes.setSelectedItem(Month.from(java.time.LocalDate.now()));
        comboAno.setSelectedItem(anoAtual);
    }
}
