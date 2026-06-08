package service;

import dao.MovimentacaoDAO;
import exceptions.EntidadeNaoEncontradaException;
import exceptions.ValidacaoException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
// import java.util.stream.Collectors; // REMOVIDO: Não é mais necessário

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

        // Preencher os objetos Veiculo e TipoDespesa completos
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

    public double buscarUltimaQuilometragemVeiculo(Long veiculoId) {
        return buscarUltimaQuilometragemVeiculoInterno(veiculoId, null);
    }


    public double buscarUltimaQuilometragemVeiculoExcluindoAtual(Long veiculoId, Long movimentacaoId) {
        return buscarUltimaQuilometragemVeiculoInterno(veiculoId, movimentacaoId);
    }


    private double buscarUltimaQuilometragemVeiculoInterno(Long veiculoId, Long movimentacaoIdExcluir) {
        List<Movimentacao> todasMovimentacoes = listarTodos();
        List<Movimentacao> movimentacoesFiltradas = new ArrayList<>();

        for (Movimentacao mov : todasMovimentacoes) {

            if (mov.getVeiculo() == null || !mov.getVeiculo().getId().equals(veiculoId)) {
                continue;
            }
            if (movimentacaoIdExcluir != null && mov.getId().equals(movimentacaoIdExcluir)) {
                continue;
            }
            if (mov.getTipoDespesa() == null || !"COMBUSTÍVEL".equalsIgnoreCase(mov.getTipoDespesa().getDescricao())) {
                continue;
            }
            if (mov.getQuilometragemAtual() <= 0) {
                continue;
            }
            movimentacoesFiltradas.add(mov);
        }

        movimentacoesFiltradas.sort(new Comparator<Movimentacao>() {
            @Override
            public int compare(Movimentacao m1, Movimentacao m2) {
                int dateComparison = m1.getData().compareTo(m2.getData());
                if (dateComparison != 0) {
                    return dateComparison;
                }
                return Double.compare(m2.getQuilometragemAtual(), m1.getQuilometragemAtual());
            }
        });

        if (!movimentacoesFiltradas.isEmpty()) {
            return movimentacoesFiltradas.get(0).getQuilometragemAtual();
        }

        return 0.0;
    }


    public boolean hasCombustivelMovimentacao(Long veiculoId) {
        List<Movimentacao> todasMovimentacoes = listarTodos();

        for (Movimentacao mov : todasMovimentacoes) {
            if (mov.getVeiculo() != null && mov.getVeiculo().getId().equals(veiculoId) &&
                mov.getTipoDespesa() != null && "COMBUSTÍVEL".equalsIgnoreCase(mov.getTipoDespesa().getDescricao())) {
                return true;
            }
        }
        return false; // Não encontrou
    }


    public void inicializarQuilometragemVeiculosExistentes() {
        List<Veiculo> todosVeiculos = veiculoService.listarTodos();
        TipoDespesa tipoCombustivel = tipoDespesaService.buscarPorDescricao("COMBUSTÍVEL");
        Random random = new Random();

        for (Veiculo veiculo : todosVeiculos) {
            if (!hasCombustivelMovimentacao(veiculo.getId())) {

                double quilometragemInicial = 10000 + (200000 - 10000) * random.nextDouble();

                quilometragemInicial = Math.round(quilometragemInicial);


                Movimentacao movimentacaoInicial = new Movimentacao(
                        veiculo,
                        tipoCombustivel,
                        "Inicialização de Quilometragem",
                        LocalDate.of(2023, 1, 1), // Data fictícia no passado
                        BigDecimal.ONE,
                        quilometragemInicial
                );

                salvar(movimentacaoInicial);
                System.out.println("Quilometragem inicial de " + quilometragemInicial + " km registrada para o veículo " + veiculo.getPlaca());
            }
        }
    }

    public List<Movimentacao> listarCombustivelPorVeiculoEPeriodo(Long veiculoId, YearMonth mesAno) {
        List<Movimentacao> todasMovimentacoes = listarTodos();
        List<Movimentacao> resultado = new ArrayList<>();

        for (Movimentacao mov : todasMovimentacoes) {
            if (mov.getVeiculo() != null && mov.getVeiculo().getId().equals(veiculoId) &&
                mov.getTipoDespesa() != null && "COMBUSTÍVEL".equalsIgnoreCase(mov.getTipoDespesa().getDescricao()) &&
                mov.getData() != null && YearMonth.from(mov.getData()).equals(mesAno)) {
                resultado.add(mov);
            }
        }
        return resultado;
    }
}
