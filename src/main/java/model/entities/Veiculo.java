package model.entities;

import model.enumss.StatusVeiculo;

import java.time.Year;

public class Veiculo {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private String categoria;
    private Year anoDeFabricacao;
    private StatusVeiculo statusVeiculo;

    public Veiculo() {
    }

    public Veiculo(String placa, String marca, String modelo, String categoria, StatusVeiculo estado, Year anoDeFabricacao) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.categoria = categoria;
        this.statusVeiculo = estado;
        this.anoDeFabricacao = anoDeFabricacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Year getAnoDeFabricacao() {
        return anoDeFabricacao;
    }

    public void setAnoDeFabricacao(Year anoDeFabricacao) {
        this.anoDeFabricacao = anoDeFabricacao;
    }

    public StatusVeiculo getStatusVeiculo() {
        return statusVeiculo;
    }

    public void setStatusVeiculo(StatusVeiculo statusVeiculo) {
        this.statusVeiculo = statusVeiculo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
