package model.entities;


import model.enumss.StatusTipoDespesa;

public class TipoDespesa {
    private Long id;
    private String descricao;
    private StatusTipoDespesa statusTipoDespesa;

    public TipoDespesa() {
    }

    public TipoDespesa(String descricao, StatusTipoDespesa statusTipoDespesa) {
        this.descricao = descricao;
        this.statusTipoDespesa = statusTipoDespesa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusTipoDespesa getStatusTipoDespesa() {
        return statusTipoDespesa;
    }

    public void setStatusTipoDespesa(StatusTipoDespesa statusTipoDespesa) {
        this.statusTipoDespesa = statusTipoDespesa;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
