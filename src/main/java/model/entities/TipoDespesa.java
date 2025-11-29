package model.entities;


public class TipoDespesa {
    private Long idTipoDespesa;
    private String descricao;

    public TipoDespesa(String descricao) {
        this.descricao = descricao;
    }

    public Long getIdTipoDespesa() {
        return idTipoDespesa;
    }

    public void setIdTipoDespesa(Long idTipoDespesa) {
        this.idTipoDespesa = idTipoDespesa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descriaco) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
