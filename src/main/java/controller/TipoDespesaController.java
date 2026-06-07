package controller;

import model.entities.TipoDespesa;
import service.TipoDespesaService;

import java.util.List;

public class TipoDespesaController {
    private TipoDespesaService tipoDespesaService = new TipoDespesaService();

    public void salvar(TipoDespesa tipoDespesa) {
        tipoDespesaService.salvar(tipoDespesa);
    }

    public void atualizar(TipoDespesa tipoDespesa) {
        tipoDespesaService.atualizar(tipoDespesa);
    }

    public List<TipoDespesa> listarTodos() {
        return tipoDespesaService.listarTodos();
    }

    public List<TipoDespesa> listarAtivos() {
        return tipoDespesaService.listarAtivos();
    }

    public List<TipoDespesa> listarInativos() {
        return tipoDespesaService.listarInativos();
    }

    public TipoDespesa buscarPorId(Long id) {
        return tipoDespesaService.buscarPorId(id);
    }
}
