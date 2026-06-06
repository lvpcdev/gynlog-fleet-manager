package service;

import dao.MovimentacaoDAO;
import exceptionss.EntidadeNaoEncontradaException;
import exceptionss.FilaVaziaException;
import exceptionss.ValidacaoException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import model.enumss.StatusMovimentacao;
import utils.Fila;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoService {

    private MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private VeiculoService veiculoService = new VeiculoService();
    private TipoDespesaService tipoDespesaService = new TipoDespesaService();

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



    public BigDecimal mediaIpvaPorAno(Year ano) {
        List<Movimentacao> movimentacoes = listarIpvaPorAno(ano);

        if (movimentacoes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal soma = BigDecimal.ZERO;

        for (Movimentacao mov : movimentacoes) {
            soma = soma.add(mov.getValor());
        }

        BigDecimal quantidade = new BigDecimal(movimentacoes.size());

        return soma.divide(quantidade, 2, RoundingMode.HALF_UP);
    }


    public BigDecimal mediaDespesasPorCategoria(String categoria){

        List<Movimentacao> movimentacoes = listarTodos();

        BigDecimal soma = BigDecimal .ZERO;
        int quantidade = 0;
        for(Movimentacao mov : movimentacoes){
            if(mov.getVeiculo().getCategoria() .equalsIgnoreCase(categoria)){
                soma = soma.add(mov.getValor());
                quantidade++;
            }
        }

        if (quantidade == 0){
            return BigDecimal .ZERO;
        }

        return soma.divide(new BigDecimal(quantidade), 2, RoundingMode.HALF_UP);
    }


    public Movimentacao aprovarProxima(Fila<Movimentacao> fila) {
        if (fila.estaVazia()) {
            throw new FilaVaziaException("Não  há movimentações pendentes na fila");
        }
        Movimentacao mov = fila.removerInicio();
        mov.setStatusMovimentacao(StatusMovimentacao.APROVADA);
        movimentacaoDAO.atualizar(mov);
        return mov;
    }

}
