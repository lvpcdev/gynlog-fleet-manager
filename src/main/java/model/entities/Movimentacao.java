package model.entities;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Movimentacao {
    private Long idMovimentacao;
    private Long idVeiculo;
    private Long idTipoDespesa;
    private String descricao;
    private LocalDate data;
    private double valor;;

    public Movimentacao(Long idVeiculo, Long idTipoDespesa, String descricao, String data, double valor) {
        this.idVeiculo = idVeiculo;
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
        setData(data);
        this.valor = valor;
    }

    public Long getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(Long idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long idVeiculo) {
        this.idVeiculo = idVeiculo;
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

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(String data) {

        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        this.data = LocalDate.parse(data, fmtData);
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Movimentacao{" +
                "idMovimentacao=" + idMovimentacao +
                ", idVeiculo=" + idVeiculo +
                ", idTipoDespesa=" + idTipoDespesa +
                ", descricao='" + descricao + '\'' +
                ", data=" + data +
                ", valor=" + valor +
                '}';
    }


}
