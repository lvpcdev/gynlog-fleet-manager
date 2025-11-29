package controller;

import model.entities.TipoDespesa;
import persistence.dao.TipoDespesaDAO;

import java.util.List;

public class TipoDespesaController {

    private final TipoDespesaDAO tipoDespesaDAO;

    public TipoDespesaController() {
        this.tipoDespesaDAO = new TipoDespesaDAO();
    }


    public void criarTipoDespesa(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            System.err.println("A descrição do tipo de despesa não pode ser vazia.");
            return;
        }

        TipoDespesa novoTipoDespesa = new TipoDespesa(descricao.trim());
        tipoDespesaDAO.salvar(novoTipoDespesa);
        System.out.println("Tipo de despesa '" + descricao + "' salvo com sucesso!");
    }


    public List<TipoDespesa> listarTodosTiposDespesa() {
        return tipoDespesaDAO.listarTodos();
    }


    public void excluirTipoDespesa(Long id) {
        if (id == null || id <= 0) {
            System.err.println("ID inválido para exclusão.");
            return;
        }

    }
}
