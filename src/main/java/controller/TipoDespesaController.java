package controller;

import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import persistence.dao.MovimentacaoDAO;
import persistence.dao.TipoDespesaDAO;

import java.util.List;

public class TipoDespesaController {

    private final TipoDespesaDAO tipoDespesaDAO;
    private final MovimentacaoDAO movimentacaoDAO;

    public TipoDespesaController() {
        this.tipoDespesaDAO = new TipoDespesaDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();
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


    public void atualizarTipoDespesa(TipoDespesa tipoDespesa) {
        if (tipoDespesa == null || tipoDespesa.getDescricao() == null || tipoDespesa.getDescricao().trim().isEmpty()) {
            System.err.println("Erro de atualização: dados do tipo de despesa são inválidos.");
            return;
        }
        tipoDespesaDAO.atualizar(tipoDespesa);
    }


    public boolean excluirTipoDespesa(Long id) {
        if (id == null || id <= 0) {
            System.err.println("ID inválido para exclusão.");
            return false;
        }


        if (tipoDespesaEmUso(id)) {
            System.err.println("Operação bloqueada: O tipo de despesa está vinculado a uma ou mais movimentações.");
            return false;
        }

        tipoDespesaDAO.excluir(id);
        return true;
    }

    private boolean tipoDespesaEmUso(Long idTipoDespesa) {
        List<Movimentacao> todasMovimentacoes = movimentacaoDAO.listarTodos();
        for (Movimentacao mov : todasMovimentacoes) {
            if (mov.getTipoDespesa().getIdTipoDespesa().equals(idTipoDespesa)) {
                return true;
            }
        }
        return false;
    }


}
