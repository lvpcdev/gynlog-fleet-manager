package dao;

import model.entities.Movimentacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MovimentacaoDAO {

    private final String caminho = "data/movimentacoes.txt";

    private DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void salvar(Movimentacao mov) {
        File arquivo = new File(caminho);


        String movimentacaoTexto = mov.getIdMovimentacao() + ";"
                                + mov.getIdVeiculo() + ";"
                                + mov.getIdTipoDespesa() + ";"
                                + mov.getDescricao() + ";"
                                + mov.getData().format(fmtData) + ";"
                                + mov.getValor();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))){
            writer.write(movimentacaoTexto);
            writer.newLine();

            System.out.println("Movimentação salva com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar movimentação: " + e.getMessage());
        }
    }
}
