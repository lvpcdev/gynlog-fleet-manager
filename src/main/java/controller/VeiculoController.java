package controller;


import model.entities.Veiculo;
import service.VeiculoService;

import java.util.List;

public class VeiculoController {
    private VeiculoService veiculoService = new VeiculoService();

    public void salvar(Veiculo veiculo) {
        veiculoService.salvar(veiculo);
    }

    public void atualizar(Veiculo veiculo) {
        veiculoService.atualizar(veiculo);
    }

    public List<Veiculo> listarTodos() {
        return veiculoService.listarTodos();
    }

    public List<Veiculo> listarAtivos() {
        return veiculoService.listarAtivos();
    }

    public List<Veiculo> listarInativos() {
        return veiculoService.listarInativos();
    }

    public Veiculo buscarPorId(Long id) {
        return veiculoService.buscarPorId(id);
    }

    public List<String> listarCategorias(){return veiculoService.listarCategorias();}
}
