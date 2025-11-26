package model.entities;


public class TipoDespesa {
    private Long idTipoDespesa;
    private String descriaco;

    public TipoDespesa(String descriaco, Long idTipoDespesa) {
        this.descriaco = descriaco;
        this.idTipoDespesa = idTipoDespesa;
    }

    public Long getIdTipoDespesa() {
        return idTipoDespesa;
    }

    public void setIdTipoDespesa(Long idTipoDespesa) {
        this.idTipoDespesa = idTipoDespesa;
    }

    public String getDescriaco() {
        return descriaco;
    }

    public void setDescriaco(String descriaco) {
        this.descriaco = descriaco;
    }

    @Override
    public String toString() {
        return "TipoDespesa{" +
                "idTipoDespesa=" + idTipoDespesa +
                ", descriaco='" + descriaco + '\'' +
                '}';
    }
}
