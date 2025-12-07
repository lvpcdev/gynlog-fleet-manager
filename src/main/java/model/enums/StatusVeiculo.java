package model.enums;

public enum StatusVeiculo {
    ATIVO("Ativo na Frota"),
    INATIVO("Inativo na Frota");

    private final String descricao;

    StatusVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusVeiculo setStatus(String descricao) {
        if(descricao.equals("ATIVO")) {
            return  ATIVO;
        } else return INATIVO;
    }
}
