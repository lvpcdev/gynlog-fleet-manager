package controller;


import model.entities.Movimentacao;
import service.MovimentacaoService;

import java.math.BigDecimal;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public class MovimentacaoController {

    private MovimentacaoService movimentacaoService = new MovimentacaoService();

    public void salvar(Movimentacao movimentacao) {
        movimentacaoService.salvar(movimentacao);
    }

    public void atualizar(Movimentacao movimentacao) {
        movimentacaoService.atualizar(movimentacao);
    }

    public void excluir(Long id) {
        movimentacaoService.excluir(id);
    }

    public List<Movimentacao> listarTodos() {
        return movimentacaoService.listarTodos();
    }

    public Movimentacao buscarPorId(Long id) {
        return movimentacaoService.buscarPorId(id);
    }

    public List<Movimentacao> listarPorVeiculo(Long id) {
        return movimentacaoService.listarPorVeiculo(id);
    }

    public BigDecimal totalPorVeiculo(Long id) {
        return movimentacaoService.totalPorVeiculo(id);
    }

    public List<Movimentacao> listarPorMes(YearMonth mesAno) {
        return movimentacaoService.listarPorMes(mesAno);
    }

    public BigDecimal totalPorMes(YearMonth mesAno) {
        return movimentacaoService.totalPorMes(mesAno);
    }

    public List<Movimentacao> listarCombustivelPorMes(YearMonth mesAno) {
        return movimentacaoService.listarCombustivelPorMes(mesAno);
    }

    public BigDecimal totalCombustivelPorMes(YearMonth mesAno) {
        return movimentacaoService.totalCombustivelPorMes(mesAno);
    }

    public List<Movimentacao> listarIpvaPorAno(Year ano) {
        return movimentacaoService.listarIpvaPorAno(ano);
    }

    public BigDecimal totalIpvaPorAno(Year ano) {
        return movimentacaoService.totalIpvaPorAno(ano);
    }

    public List<Movimentacao> listarMultasPorVeiculo(Long id, Year ano) {
        return movimentacaoService.listarMultasPorVeiculo(id, ano);
    }

    public BigDecimal totalMultasPorVeiculo(Long id, Year ano) {
        return movimentacaoService.totalMultasPorVeiculo(id, ano);
    }

    // NOVO: Método para buscar a última quilometragem registrada para um veículo
    public double buscarUltimaQuilometragemVeiculo(Long veiculoId) {
        return movimentacaoService.buscarUltimaQuilometragemVeiculo(veiculoId);
    }

    // NOVO: Método para buscar a última quilometragem registrada para um veículo, excluindo uma movimentação específica
    public double buscarUltimaQuilometragemVeiculoExcluindoAtual(Long veiculoId, Long movimentacaoId) {
        return movimentacaoService.buscarUltimaQuilometragemVeiculoExcluindoAtual(veiculoId, movimentacaoId);
    }

    // NOVO: Método para verificar se um veículo possui movimentações de combustível
    public boolean hasCombustivelMovimentacao(Long veiculoId) {
        return movimentacaoService.hasCombustivelMovimentacao(veiculoId);
    }

    // NOVO: Método para listar movimentações de combustível por veículo e período
    public List<Movimentacao> listarCombustivelPorVeiculoEPeriodo(Long veiculoId, YearMonth mesAno) {
        return movimentacaoService.listarCombustivelPorVeiculoEPeriodo(veiculoId, mesAno);
    }
}
