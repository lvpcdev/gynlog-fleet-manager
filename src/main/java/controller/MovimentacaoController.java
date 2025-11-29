package controller;

import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import persistence.dao.MovimentacaoDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MovimentacaoController {

    private final MovimentacaoDAO movimentacaoDAO;


    public MovimentacaoController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    public boolean registrarMovimentacao(Veiculo veiculo, TipoDespesa tipoDespesa, String descricao, LocalDate data, BigDecimal valor) {
        if (veiculo == null) {
            System.err.println("Erro de registro: O veículo não pode ser nulo.");
            return false;
        }
        if (tipoDespesa == null) {
            System.err.println("Erro de registro: O tipo de despesa não pode ser nulo.");
            return false;
        }
        if (data == null || data.isAfter(LocalDate.now())) {
            System.err.println("Erro de registro: A data é inválida ou está no futuro.");
            return false;
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Erro de registro: O valor deve ser maior que zero.");
            return false;
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            descricao = tipoDespesa.getDescricao();
        }


        Movimentacao novaMovimentacao = new Movimentacao(veiculo, tipoDespesa, descricao, data, valor);


        movimentacaoDAO.salvar(novaMovimentacao);
        System.out.println("Movimentação registrada com sucesso para o veículo " + veiculo.getPlaca());
        return true;
    }


    public List<Movimentacao> listarTodasMovimentacoes() {
        return movimentacaoDAO.listarTodos();
    }


    public void excluirMovimentacao(Long id) {
        if (id == null || id <= 0) {
            System.err.println("ID inválido para exclusão.");
            return;
        }
        movimentacaoDAO.excluir(id);
        System.out.println("Movimentação com ID " + id + " excluída (se existia).");
    }
}
