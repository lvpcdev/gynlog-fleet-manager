package model.entities;

import java.time.LocalDate;

public abstract class Despesa {
    private int id;
    private LocalDate data;
    private double valor;
    private String descricao;

    public Despesa(LocalDate data, int id, double valor, String descricao) {
        this.data = data;
        this.id = id;
        this.valor = valor;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public abstract  String getTipoDespesa();

}
