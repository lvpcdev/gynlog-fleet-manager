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

    public BigDecimal mediaIpvaPorAno(Year ano){
        return movimentacaoService.mediaIpvaPorAno(ano);
    }

    public BigDecimal mediaDespesasPorCategoria(String categoria) {
        return movimentacaoService.mediaDespesasPorCategoria(categoria);
    }
  
  public Movimentacao aprovarProxima(Fila<Movimentacao> fila) {
        return movimentacaoService.aprovarProxima(fila);
    }
    


}
