package controller;

import model.entities.Movimentacao;
import model.entities.Veiculo;
import model.enums.StatusVeiculo;
import persistence.dao.MovimentacaoDAO;
import persistence.dao.VeiculoDAO;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class RelatorioController {

    private final MovimentacaoDAO movimentacaoDAO;
    private final VeiculoDAO veiculoDAO;
    private final DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat fmtMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public RelatorioController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
        this.veiculoDAO = new VeiculoDAO();
    }

    private List<Movimentacao> getTodasMovimentacoes() {
        return movimentacaoDAO.listarTodos();
    }

    private Optional<Veiculo> buscarVeiculoPorId(Long idVeiculo) {
        return veiculoDAO.listarTodos().stream()
                .filter(v -> v.getIdVeiculo().equals(idVeiculo))
                .findFirst();
    }


    public String gerarRelatorioDespesasPorVeiculo(Long idVeiculo) {
        StringBuilder relatorio = new StringBuilder();


        Optional<Veiculo> veiculoOpt = buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            return "Veículo com ID " + idVeiculo + " não encontrado.";
        }
        Veiculo veiculo = veiculoOpt.get();

        relatorio.append("Relatório: Despesas do Veículo\n");
        relatorio.append("-------------------------------------\n");
        relatorio.append(String.format("Veículo: %s - %s\n\n", veiculo.getPlaca(), veiculo.getModelo()));
        relatorio.append(String.format("%-12s | %-15s | %-25s | %s\n", "Data", "Tipo", "Descrição", "Valor"));
        relatorio.append("----------------------------------------------------------------------\n");

        List<Movimentacao> despesasDoVeiculo = getTodasMovimentacoes().stream()
                .filter(mov -> mov.getVeiculo().getIdVeiculo().equals(idVeiculo))
                .collect(Collectors.toList());


        despesasDoVeiculo.forEach(mov -> relatorio.append(String.format("%-12s | %-15s | %-25s | %s\n",
                mov.getData().format(fmtData),
                mov.getTipoDespesa().getDescricao(),
                mov.getDescricao(),
                fmtMoeda.format(mov.getValor()))));


        BigDecimal totalDespesas = despesasDoVeiculo.stream()
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        relatorio.append("----------------------------------------------------------------------\n");

        relatorio.append(String.format("%56s %s", "TOTAL:", fmtMoeda.format(totalDespesas)));

        return relatorio.toString();
    }

    public String gerarRelatorioSomaGeralMes(int mes, int ano) {
        StringBuilder relatorio = new StringBuilder();

        String nomeMes = java.time.Month.of(mes).getDisplayName(java.time.format.TextStyle.FULL, new Locale("pt", "BR"));
        nomeMes = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);

        relatorio.append("Relatório: Despesas Gerais da Frota\n");
        relatorio.append("-------------------------------------\n");
        relatorio.append(String.format("Mês/Ano de Referência: %s de %d\n\n", nomeMes, ano));

        relatorio.append(String.format("%-12s | %-15s | %-15s | %-25s | %s\n", "Data", "Veículo", "Tipo", "Descrição", "Valor"));
        relatorio.append("------------------------------------------------------------------------------------------\n");

        List<Movimentacao> despesasDoMes = getTodasMovimentacoes().stream()
                .filter(mov -> mov.getData().getMonthValue() == mes && mov.getData().getYear() == ano)
                .collect(Collectors.toList());


        despesasDoMes.forEach(mov -> relatorio.append(String.format("%-12s | %-15s | %-15s | %-25s | %s\n",
                mov.getData().format(fmtData),
                mov.getVeiculo().getPlaca(),
                mov.getTipoDespesa().getDescricao(),
                mov.getDescricao(),
                fmtMoeda.format(mov.getValor()))));


        BigDecimal totalDespesas = despesasDoMes.stream()
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        relatorio.append("------------------------------------------------------------------------------------------\n");

        relatorio.append(String.format("%75s %s", "TOTAL GERAL:", fmtMoeda.format(totalDespesas)));

        return relatorio.toString();
    }

    public BigDecimal calcularTotalPorTipoDespesaNoMes(String descricaoTipoDespesa, int mes, int ano) {
        return getTodasMovimentacoes().stream()
                .filter(mov -> mov.getData().getMonthValue() == mes &&
                        mov.getData().getYear() == ano &&
                        mov.getTipoDespesa().getDescricao().equalsIgnoreCase(descricaoTipoDespesa))
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalPorTipoDespesaNoAno(String descricaoTipoDespesa, int ano) {
        return getTodasMovimentacoes().stream()
                .filter(mov -> mov.getData().getYear() == ano &&
                        mov.getTipoDespesa().getDescricao().equalsIgnoreCase(descricaoTipoDespesa))
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String gerarRelatorioVeiculosInativos() {
        StringBuilder relatorio = new StringBuilder();

        relatorio.append("Relatório: Veículos Inativos na Frota\n");
        relatorio.append("--------------------------------------\n");
        relatorio.append(String.format("%-5s | %-10s | %-20s | %s\n", "ID", "Placa", "Marca", "Modelo"));
        relatorio.append("----------------------------------------------------------\n");

        veiculoDAO.listarTodos().stream()
                .filter(v -> v.getStatusVeiculo() == StatusVeiculo.INATIVO)
                .forEach(v -> relatorio.append(String.format("%-5d | %-10s | %-20s | %s\n",
                        v.getIdVeiculo(),
                        v.getPlaca(),
                        v.getMarca(),
                        v.getModelo())));

        return relatorio.toString();
    }

    public String gerarRelatorioMultasPorVeiculoAno(Long idVeiculo, int ano) {
        StringBuilder relatorio = new StringBuilder();

        Optional<Veiculo> veiculoOpt = buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            return "Veículo com ID " + idVeiculo + " não encontrado.";
        }
        Veiculo veiculo = veiculoOpt.get();

        relatorio.append("Relatório: Multas por Veículo no Ano\n");
        relatorio.append("---------------------------------------\n");
        relatorio.append(String.format("Veículo: %s - %s\n", veiculo.getPlaca(), veiculo.getModelo()));
        relatorio.append(String.format("Ano: %d\n\n", ano));
        relatorio.append(String.format("%-12s | %-25s | %s\n", "Data", "Descrição", "Valor"));
        relatorio.append("------------------------------------------------------\n");

        getTodasMovimentacoes().stream()
                .filter(mov -> mov.getVeiculo().getIdVeiculo().equals(idVeiculo) &&
                        mov.getData().getYear() == ano &&
                        mov.getTipoDespesa().getDescricao().equalsIgnoreCase("Multa"))
                .forEach(mov -> relatorio.append(String.format("%-12s | %-25s | %s\n",
                        mov.getData().format(fmtData),
                        mov.getDescricao(),
                        fmtMoeda.format(mov.getValor()))));

        return relatorio.toString();
    }
}
