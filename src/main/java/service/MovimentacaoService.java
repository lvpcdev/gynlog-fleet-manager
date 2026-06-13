package service;

import dao.MovimentacaoDAO;
import exceptions.EntidadeNaoEncontradaException;
import exceptions.FilaVaziaException;
import exceptions.ValidacaoException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import model.enums.StatusMovimentacao;
import util.Fila;
import util.SelectionSort;

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

    public List<Movimentacao> listarAprovadas() {
        List<Movimentacao> movimentacoes = listarTodos();
        List<Movimentacao> resultado = new ArrayList<>();

        for (Movimentacao mov : movimentacoes) {
            if (mov.getStatusMovimentacao().equals(StatusMovimentacao.APROVADA)) {
                resultado.add(mov);
            }
        }

        return resultado;
    }

    public Fila<Movimentacao> listarPendentes() {
        List<Movimentacao> movimentacoes = listarTodos();
        Fila<Movimentacao> fila = new Fila<Movimentacao>();

        for (Movimentacao mov : movimentacoes) {
            if (mov.getStatusMovimentacao().equals(StatusMovimentacao.PENDENTE)) {
                fila.inserirFim(mov);
            }
        }

        return fila;
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
        List<Movimentacao> movimentacoes = listarAprovadas();
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
        List<Movimentacao> movimentacoes = listarAprovadas();
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
        List<Movimentacao> movimentacoes = listarAprovadas();
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
        List<Movimentacao> movimentacoes = listarAprovadas();
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
        List<Movimentacao> movimentacoes = listarAprovadas();
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

        List<Movimentacao> movimentacoes = listarAprovadas();

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

    public List<Veiculo> listarVeiculosOrdenadosPorCusto() {
        List<Veiculo> veiculos = veiculoService.listarTodos();
        List<Movimentacao> movimentacoes = listarAprovadas();

        List<Movimentacao> combustivel = new ArrayList<>();
        for (Movimentacao mov : movimentacoes) {
            if (mov.getTipoDespesa().getDescricao().equalsIgnoreCase("COMBUSTÍVEL")) {
                combustivel.add(mov);
            }
        }

        List<Veiculo> veiculosComCombustivel = new ArrayList<>();
        for (Veiculo v : veiculos) {
            for (Movimentacao mov : combustivel) {
                if (mov.getVeiculo().getId().equals(v.getId())) {
                    veiculosComCombustivel.add(v);
                    break;
                }
            }
        }
        return SelectionSort.ordenarVeiculosPorTotalDespesa(veiculosComCombustivel, movimentacoes);
    }

    public double buscarUltimaQuilometragemVeiculo(Long veiculoId) {
        return buscarUltimaQuilometragemVeiculoInterno(veiculoId, null);
    }

    public double buscarUltimaQuilometragemVeiculoExcluindoAtual(Long veiculoId, Long movimentacaoId) {
        return buscarUltimaQuilometragemVeiculoInterno(veiculoId, movimentacaoId);
    }

    private double buscarUltimaQuilometragemVeiculoInterno(Long veiculoId, Long movimentacaoIdExcluir) {
        List<Movimentacao> todasMovimentacoes = listarTodos();
        List<Movimentacao> filtradas = new ArrayList<>();

        for (Movimentacao mov : todasMovimentacoes) {
            if (mov.getVeiculo() == null || !mov.getVeiculo().getId().equals(veiculoId)) continue;
            if (movimentacaoIdExcluir != null && mov.getId().equals(movimentacaoIdExcluir)) continue;
            if (mov.getTipoDespesa() == null || !mov.getTipoDespesa().getDescricao().equalsIgnoreCase("COMBUSTÍVEL")) continue;
            if (mov.getQuilometragemAtual() == null || mov.getQuilometragemAtual() <= 0) continue;
            filtradas.add(mov);
        }

        for (int i = 0; i < filtradas.size() - 1; i++) {
            int indiceMenor = i;
            for (int j = i + 1; j < filtradas.size(); j++) {
                if (filtradas.get(j).getData().isBefore(filtradas.get(indiceMenor).getData())) {
                    indiceMenor = j;
                }
            }
            if (indiceMenor != i) {
                Movimentacao temp = filtradas.get(i);
                filtradas.set(i, filtradas.get(indiceMenor));
                filtradas.set(indiceMenor, temp);
            }
        }

        if (!filtradas.isEmpty()) {
            return filtradas.get(filtradas.size() - 1).getQuilometragemAtual();
        }

        return 0.0;
    }

    public boolean existeCombustivelMovimentacao(Long veiculoId) {
        List<Movimentacao> todasMovimentacoes = listarTodos();

        for (Movimentacao mov : todasMovimentacoes) {
            if (mov.getVeiculo() != null && mov.getVeiculo().getId().equals(veiculoId)
                    && mov.getTipoDespesa() != null
                    && mov.getTipoDespesa().getDescricao().equalsIgnoreCase("COMBUSTÍVEL")) {
                return true;
            }
        }
        return false;
    }

    public BigDecimal consumoMedioPorVeiculo(Long veiculoId) {
        List<Movimentacao> movimentacoes = listarAprovadas();
        List<Movimentacao> filtradas = new ArrayList<>();

        for (Movimentacao mov : movimentacoes) {
            if (mov.getVeiculo().getId().equals(veiculoId)
                    && mov.getTipoDespesa().getDescricao().equalsIgnoreCase("COMBUSTÍVEL")
                    && mov.getQuilometragemAtual() != null
                    && mov.getQuilometragemAtual() > 0) {
                filtradas.add(mov);
            }
        }

        if (filtradas.size() < 2) {
            return BigDecimal.ZERO;
        }

        for (int i = 0; i < filtradas.size() - 1; i++) {
            int indiceMenor = i;
            for (int j = i + 1; j < filtradas.size(); j++) {
                if (filtradas.get(j).getData().isBefore(filtradas.get(indiceMenor).getData())) {
                    indiceMenor = j;
                }
            }
            if (indiceMenor != i) {
                Movimentacao temp = filtradas.get(i);
                filtradas.set(i, filtradas.get(indiceMenor));
                filtradas.set(indiceMenor, temp);
            }
        }

        double kmInicial = filtradas.get(0).getQuilometragemAtual();
        double kmFinal = filtradas.get(filtradas.size() - 1).getQuilometragemAtual();
        double kmRodados = kmFinal - kmInicial;

        if (kmRodados <= 0) return BigDecimal.ZERO;

        BigDecimal totalValor = BigDecimal.ZERO;
        for (int i = 1; i < filtradas.size(); i++) {
            totalValor = totalValor.add(filtradas.get(i).getValor());
        }

        if (totalValor.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return new BigDecimal(kmRodados).divide(totalValor, 2, RoundingMode.HALF_UP);
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
