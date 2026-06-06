package service;

import dao.TipoDespesaDAO;
import exceptionss.EntidadeNaoEncontradaException;
import exceptionss.ValidacaoException;
import model.entities.TipoDespesa;
import model.enumss.StatusTipoDespesa;

import java.util.ArrayList;
import java.util.List;

public class TipoDespesaService {
    private TipoDespesaDAO tipoDespesaDAO = new TipoDespesaDAO();

    public void salvar(TipoDespesa tipoDespesa) {
        if (tipoDespesa.getDescricao() == null || tipoDespesa.getDescricao().isEmpty()) {
            throw new ValidacaoException("Descrição obrigatória");
        }

        if (existeDescricao(tipoDespesa.getDescricao())) {
            throw new ValidacaoException("Descrição já cadastrada");
        }

        tipoDespesaDAO.salvar(tipoDespesa);
    }

    public void atualizar(TipoDespesa tipoDespesa) {
        if (tipoDespesa.getDescricao() == null || tipoDespesa.getDescricao().isEmpty()) {
            throw new ValidacaoException("Descrição obrigatória");
        }

        if (existeDescricaoParaOutroTipoDespesa(tipoDespesa.getDescricao(), tipoDespesa.getId())) {
            throw new ValidacaoException("Descrição já cadastrada");
        }

        tipoDespesaDAO.atualizar(tipoDespesa);
    }

    public List<TipoDespesa> listarTodos() {
        return tipoDespesaDAO.listarTodos();
    }

    public List<TipoDespesa> listarInativos() {
        List<TipoDespesa> tipoDespesas = listarTodos();
        List<TipoDespesa> tipoDespesasInativos = new ArrayList<>();

        for (TipoDespesa v : tipoDespesas) {
            if (v.getStatusTipoDespesa().equals(StatusTipoDespesa.INATIVO)) {
                tipoDespesasInativos.add(v);
            }
        }
        return tipoDespesasInativos;
    }

    public List<TipoDespesa> listarAtivos() {
        List<TipoDespesa> tipoDespesas = listarTodos();
        List<TipoDespesa> tipoDespesasAtivos = new ArrayList<>();

        for (TipoDespesa v : tipoDespesas) {
            if (v.getStatusTipoDespesa().equals(StatusTipoDespesa.ATIVO)) {
                tipoDespesasAtivos.add(v);
            }
        }
        return tipoDespesasAtivos;
    }

    public TipoDespesa buscarPorId(Long id) {
        List<TipoDespesa> tipoDespesas = listarTodos();

        for (TipoDespesa td : tipoDespesas) {
            if (td.getId().equals(id)) {
                return td;
            }
        }

        throw new EntidadeNaoEncontradaException("Tipo de despesa com id " + id + " não encontrado");
    }

    private boolean existeDescricao(String descricao) {
        List<TipoDespesa> tipoDespesas = listarTodos();

        for (TipoDespesa td : tipoDespesas) {
            if (td.getDescricao().equalsIgnoreCase(descricao)) {
                return true;
            }
        }

        return false;
    }

    private boolean existeDescricaoParaOutroTipoDespesa(String descricao, Long id) {
        List<TipoDespesa> tipoDespesas = listarTodos();

        for (TipoDespesa v : tipoDespesas) {
            if (v.getDescricao().equalsIgnoreCase(descricao) && !v.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }
}
