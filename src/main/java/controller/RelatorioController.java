package controller;

import model.entities.Movimentacao;
import model.entities.Veiculo;
import persistence.dao.MovimentacaoDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RelatorioController {

    private final MovimentacaoDAO movimentacaoDAO;

    public RelatorioController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    private List<Movimentacao> getTodasMovimentacoes() {
        return movimentacaoDAO.listarTodos();
    }


    // Relatório 1: Filtra todas as despesas de um veículo específico.

    public List<Movimentacao> gerarRelatorioDespesasPorVeiculo(Veiculo veiculo) {
        List<Movimentacao> todasMovimentacoes = getTodasMovimentacoes();
        List<Movimentacao> despesasDoVeiculo = new ArrayList<>();

        for (Movimentacao mov : todasMovimentacoes) {
            if (mov.getVeiculo().getIdVeiculo().equals(veiculo.getIdVeiculo())) {
                despesasDoVeiculo.add(mov);
            }
        }
        return despesasDoVeiculo;
    }


     // Relatório 2: Calcula o somatório de todas as despesas em um mês/ano específico.

    public BigDecimal calcularTotalDespesasPorMes(int mes, int ano) {
        List<Movimentacao> todasMovimentacoes = getTodasMovimentacoes();
        BigDecimal total = BigDecimal.ZERO;

        for (Movimentacao mov : todasMovimentacoes) {
            if (mov.getData().getMonthValue() == mes && mov.getData().getYear() == ano) {
                total = total.add(mov.getValor());
            }
        }
        return total;
    }

    // Relatório 3: Calcula o total gasto com um tipo de despesa específico em um mês/ano.

    public BigDecimal calcularTotalPorTipoDespesaNoMes(String descricaoTipoDespesa, int mes, int ano) {
        List<Movimentacao> todasMovimentacoes = getTodasMovimentacoes();
        BigDecimal total = BigDecimal.ZERO;

        for (Movimentacao mov : todasMovimentacoes) {
            boolean mesmoTipo = mov.getTipoDespesa().getDescricao().equalsIgnoreCase(descricaoTipoDespesa);
            boolean mesmoMes = mov.getData().getMonthValue() == mes;
            boolean mesmoAno = mov.getData().getYear() == ano;

            if (mesmoTipo && mesmoMes && mesmoAno) {
                total = total.add(mov.getValor());
            }
        }
        return total;
    }


     // Relatório 4 e 6 : Gera um relatório de um tipo de despesa para um veículo em um ano.

    public List<Movimentacao> gerarRelatorioAnualPorTipo(String descricaoTipoDespesa, int ano) {
        List<Movimentacao> todasMovimentacoes = getTodasMovimentacoes();
        List<Movimentacao> resultado = new ArrayList<>();

        for (Movimentacao mov : todasMovimentacoes) {
            boolean mesmoTipo = mov.getTipoDespesa().getDescricao().equalsIgnoreCase(descricaoTipoDespesa);
            boolean mesmoAno = mov.getData().getYear() == ano;

            if (mesmoTipo && mesmoAno) {
                resultado.add(mov);
            }
        }
        return resultado;
    }
}
