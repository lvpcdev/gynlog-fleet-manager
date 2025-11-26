package model;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Veiculo {
    private int idVeiculo;
    private String placa;
    private String marca;
    private String modelo;
    private YearMonth anoDeFabricacao;
    private boolean estado;

    public Veiculo(String placa, String marca, String modelo, boolean estado, String anoDeFabricacao) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.estado = estado;
        setAnoDeFabricacao(anoDeFabricacao);
    }

    public int getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(int idVeiculo) {
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

    public YearMonth getAnoDeFabricacao() {
        return anoDeFabricacao;
    }

    public void setAnoDeFabricacao(String anoDeFabricacaoString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

        this.anoDeFabricacao = YearMonth.parse(anoDeFabricacaoString, formatter);
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public static void main(String[] args) {
        Veiculo carro = new Veiculo("123","123","abc", false, "05/2020");

        System.out.println(carro.getAnoDeFabricacao());
    }
}
