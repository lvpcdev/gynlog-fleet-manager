package model.entities;

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
    private boolean estado;

    public Veiculo(String placa, String marca, String modelo, boolean estado, String anoDeFabricacao) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.estado = estado;
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

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }


}
