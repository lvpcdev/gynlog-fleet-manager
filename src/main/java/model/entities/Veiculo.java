package model.entities;

import model.enums.StatusVeiculo;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Veiculo {
    private Long idVeiculo;
    private String placa;
    private String marca;
    private String modelo;
    private Year anoDeFabricacao;
    private StatusVeiculo statusVeiculo;

    public Veiculo(String placa, String marca, String modelo, StatusVeiculo estado, String anoDeFabricacao) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.statusVeiculo = estado;
        setAnoDeFabricacao(anoDeFabricacao);
    }

    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long idVeiculo) {
        this.idVeiculo = idVeiculo;
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

    public void setAnoDeFabricacao(String anoDeFabricacaoString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");

        this.anoDeFabricacao = Year.parse(anoDeFabricacaoString, formatter);
    }

    public StatusVeiculo getStatusVeiculo() {
        return statusVeiculo;
    }

    public void setStatusVeiculo(StatusVeiculo statusVeiculo) {
        this.statusVeiculo = statusVeiculo;
    }
}
