package service;

import dao.MovimentacaoDAO;
import exceptions.EntidadeNaoEncontradaException;
import exceptions.ValidacaoException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;

import java.math.BigDecimal;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoService {

    private MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private VeiculoService veiculoService = new VeiculoService();
    private TipoDespesaService tipoDespesaService = new TipoDespesaService();

    // ADICIONAR EM MovimentacaoService
    public static class VeiculoCusto {
        private Veiculo veiculo;
        private BigDecimal custoTotal;

        public VeiculoCusto(Veiculo veiculo, BigDecimal custoTotal) {
            this.veiculo = veiculo;
            this.custoTotal = custoTotal;
        }

        public Veiculo getVeiculo() { return veiculo; }
        public BigDecimal getCustoTotal() { return custoTotal; }
    }

    public void salvar(Movimentacao movimentacao) {
        if (movimentacao.getVeiculo() == null) {
            throw new ValidacaoException("Veículo obrigatório");
        }

        if (movimentacao.getTipoDespesa() == null) {
            throw new ValidacaoException("Tipo de despesa obrigatório");
        }

        if (movimentacao.getData() == null) {
            throw new ValidacaoException("Data obrigatória");
        }

        if (movimentacao.getDescricao() == null || movimentacao.getDescricao().isEmpty()) {
            throw  new ValidacaoException("Descrição obrigatória");
        }

        if (movimentacao.getValor() == null) {
            throw new ValidacaoException("Valor obrigatório");
        }

        if (movimentacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Valor deve ser maior que zero");
        }

        movimentacaoDAO.salvar(movimentacao);
    }

    public void atualizar(Movimentacao movimentacao) {
        if (movimentacao.getVeiculo() == null) {
            throw new ValidacaoException("Veículo obrigatório");
        }

        if (movimentacao.getTipoDespesa() == null) {
            throw new ValidacaoException("Tipo de despesa obrigatório");
        }

        if (movimentacao.getData() == null) {
            throw new ValidacaoException("Data obrigatória");
        }

        if (movimentacao.getDescricao() == null || movimentacao.getDescricao().isEmpty()) {
            throw  new ValidacaoException("Descrição obrigatória");
        }

        if (movimentacao.getValor() == null) {
            throw new ValidacaoException("Valor obrigatório");
        }

        if (movimentacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Valor deve ser maior que zero");
        }

        movimentacaoDAO.atualizar(movimentacao);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        movimentacaoDAO.excluir(id);
    }

    public List<Movimentacao> listarTodos() {
        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodos();

        for (Movimentacao mov : movimentacoes) {
            Veiculo veiculo = veiculoService.buscarPorId(mov.getVeiculo().getId());
            TipoDespesa tipoDespesa = tipoDespesaService.buscarPorId(mov.getTipoDespesa().getId());

            mov.setVeiculo(veiculo);
            mov.setTipoDespesa(tipoDespesa);
        }

        return movimentacoes;
    }

    public Movimentacao buscarPorId(Long id) {
        List<Movimentacao> movimentacoes = listarTodos();

        for (Movimentacao mov : movimentacoes) {
            if (mov.getId().equals(id)) {
                return mov;
            }
        }

        throw new EntidadeNaoEncontradaException("Movimentação com id " + id + " não encontrado");
    }

    public List<Movimentacao> listarPorVeiculo(Long id){
        List<Movimentacao> movimentacoes = listarTodos();
        List<Movimentacao> resultado = new ArrayList<>();


        for (Movimentacao mov : movimentacoes) {
            if(mov.getVeiculo().getId().equals(id)) {
                resultado.add(mov);
            }
        }

        return resultado;
    }

    public BigDecimal totalPorVeiculo(Long id) {
        List<Movimentacao> movimentacoes = listarPorVeiculo(id);
        BigDecimal total = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            total = total.add(mov.getValor());
        }
        return total;
    }

    public List<Movimentacao> listarPorMes(YearMonth mesAno) {
        List<Movimentacao> movimentacoes = listarTodos();
        List<Movimentacao> resultado = new ArrayList<>();


        for (Movimentacao mov : movimentacoes) {
            if (YearMonth.from(mov.getData()).equals(mesAno)) {
                resultado.add(mov);
            }
        }

        return resultado;
    }

    public BigDecimal totalPorMes(YearMonth mesAno) {
        List<Movimentacao> movimentacoes = listarPorMes(mesAno);
        BigDecimal total = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            total = total.add(mov.getValor());
        }
        return total;
    }

    public List<Movimentacao> listarCombustivelPorMes(YearMonth mesAno) {
        List<Movimentacao> movimentacoes = listarTodos();
        List<Movimentacao> resultado = new ArrayList<>();


        for (Movimentacao mov : movimentacoes) {
            if (YearMonth.from(mov.getData()).equals(mesAno) && mov.getTipoDespesa().getDescricao().equalsIgnoreCase("Combustível")) {
                resultado.add(mov);
            }
        }

        return resultado;
    }

    public BigDecimal totalCombustivelPorMes(YearMonth mesAno) {
        List<Movimentacao> movimentacoes = listarCombustivelPorMes(mesAno);
        BigDecimal total = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            total = total.add(mov.getValor());
        }
        return total;
    }

    public List<Movimentacao> listarIpvaPorAno(Year ano) {
        List<Movimentacao> movimentacoes = listarTodos();
        List<Movimentacao> resultado = new ArrayList<>();


        for (Movimentacao mov : movimentacoes) {
            if (Year.from(mov.getData()).equals(ano) && mov.getTipoDespesa().getDescricao().equalsIgnoreCase("IPVA")) {
                resultado.add(mov);
            }
        }

        return resultado;
    }

    public BigDecimal totalIpvaPorAno(Year ano) {
        List<Movimentacao> movimentacoes = listarIpvaPorAno(ano);
        BigDecimal total = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            total = total.add(mov.getValor());
        }
        return total;
    }

    public List<Movimentacao> listarMultasPorVeiculo(Long id, Year ano) {
        List<Movimentacao> movimentacoes = listarTodos();
        List<Movimentacao> resultado = new ArrayList<>();


        for (Movimentacao mov : movimentacoes) {
            if (Year.from(mov.getData()).equals(ano)
                    && mov.getVeiculo().getId().equals(id)
                    && mov.getTipoDespesa().getDescricao().equalsIgnoreCase("Multa")) {
                resultado.add(mov);
            }
        }

        return resultado;
    }

    public BigDecimal totalMultasPorVeiculo(Long id, Year ano) {
        List<Movimentacao> movimentacoes = listarMultasPorVeiculo(id, ano);
        BigDecimal total = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            total = total.add(mov.getValor());
        }
        return total;
    }

    // ADICIONAR EM MovimentacaoService
    /**
     * Identifica os veículos com maior e menor custo total de despesas.
     * Utiliza o algoritmo Selection Sort para ordenação manual.
     * 
     * Por que Selection Sort: É um algoritmo de ordenação simples (O(n^2)) que atende 
     * aos requisitos acadêmicos de implementação manual, permitindo identificar 
     * facilmente os extremos após a ordenação.
     */
    public List<VeiculoCusto> obterMaiorMenorCusto() {
        List<Veiculo> veiculos = veiculoService.listarTodos();
        List<VeiculoCusto> listaCustos = new ArrayList<>();

        for (Veiculo v : veiculos) {
            BigDecimal total = totalPorVeiculo(v.getId());
            listaCustos.add(new VeiculoCusto(v, total));
        }

        if (listaCustos.isEmpty()) {
            return listaCustos;
        }

        // Implementação do Selection Sort
        int n = listaCustos.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (listaCustos.get(j).getCustoTotal().compareTo(listaCustos.get(minIdx).getCustoTotal()) < 0) {
                    minIdx = j;
                }
            }
            // Swap
            VeiculoCusto temp = listaCustos.get(minIdx);
            listaCustos.set(minIdx, listaCustos.get(i));
            listaCustos.set(i, temp);
        }

        List<VeiculoCusto> resultado = new ArrayList<>();
        // O primeiro é o menor após a ordenação
        resultado.add(listaCustos.get(0));
        // O último é o maior
        if (listaCustos.size() > 1) {
            resultado.add(listaCustos.get(listaCustos.size() - 1));
        }

        return resultado;
    }
}
