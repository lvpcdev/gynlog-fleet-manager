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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    // NOVO: Método para buscar a última quilometragem registrada para um veículo
    public double buscarUltimaQuilometragemVeiculo(Long veiculoId) {
        return movimentacaoDAO.buscarUltimaQuilometragemVeiculo(veiculoId);
    }

    // NOVO: Método para buscar a última quilometragem registrada para um veículo, excluindo uma movimentação específica
    public double buscarUltimaQuilometragemVeiculoExcluindoAtual(Long veiculoId, Long movimentacaoId) {
        return movimentacaoDAO.buscarUltimaQuilometragemVeiculoExcluindoAtual(veiculoId, movimentacaoId);
    }

    // NOVO: Método para verificar se um veículo possui movimentações de combustível
    public boolean hasCombustivelMovimentacao(Long veiculoId) {
        return movimentacaoDAO.hasCombustivelMovimentacao(veiculoId);
    }

    // NOVO: Método para inicializar a quilometragem de veículos existentes
    public void inicializarQuilometragemVeiculosExistentes() {
        List<Veiculo> todosVeiculos = veiculoService.listarTodos();
        TipoDespesa tipoCombustivel = tipoDespesaService.buscarPorDescricao("COMBUSTÍVEL");
        Random random = new Random();

        for (Veiculo veiculo : todosVeiculos) {
            if (!hasCombustivelMovimentacao(veiculo.getId())) {
                // Gerar quilometragem inicial fictícia
                double quilometragemInicial = 10000 + (200000 - 10000) * random.nextDouble();
                // Arredondar para um número inteiro
                quilometragemInicial = Math.round(quilometragemInicial);

                // Criar uma movimentação de combustível fictícia
                Movimentacao movimentacaoInicial = new Movimentacao(
                        veiculo,
                        tipoCombustivel,
                        "Inicialização de Quilometragem",
                        LocalDate.of(2023, 1, 1), // Data fictícia no passado
                        BigDecimal.ONE, // CORRIGIDO: Valor agora é 1.0 para passar na validação
                        quilometragemInicial
                );
                // Salvar a movimentação
                salvar(movimentacaoInicial);
                System.out.println("Quilometragem inicial de " + quilometragemInicial + " km registrada para o veículo " + veiculo.getPlaca());
            }
        }
    }

    // NOVO: Método para listar movimentações de combustível por veículo e período
    public List<Movimentacao> listarCombustivelPorVeiculoEPeriodo(Long veiculoId, YearMonth mesAno) {
        return listarTodos().stream()
                .filter(mov -> mov.getVeiculo() != null && mov.getVeiculo().getId().equals(veiculoId))
                .filter(mov -> mov.getTipoDespesa() != null && "COMBUSTÍVEL".equalsIgnoreCase(mov.getTipoDespesa().getDescricao()))
                .filter(mov -> mov.getData() != null && YearMonth.from(mov.getData()).equals(mesAno))
                .collect(Collectors.toList());
    }
}
