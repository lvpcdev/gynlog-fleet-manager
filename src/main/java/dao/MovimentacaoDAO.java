package dao;

import model.entities.Movimentacao;

import java.io.*;
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

    public void excluir(Long idParaExcluir) {
        File arquivoOriginal = new File("data/movimentacoes.txt");
        File arquivoTemp = new File("data/movimentacoes_temp.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                String[] partes = linha.split(";");
                Long idAtual = Long.parseLong(partes[0]);


                if (idAtual.equals(idParaExcluir)) {
                    continue;
                }

                bw.write(linha);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar exclusão: " + e.getMessage());
        }


        if (arquivoOriginal.delete()) {
            boolean sucesso = arquivoTemp.renameTo(arquivoOriginal);

            if (sucesso) {
                System.out.println("Registro excluído com sucesso!");
            } else {
                System.err.println("Erro ao renomear o arquivo temporário.");
            }
        } else {
            System.err.println("Não foi possível apagar o arquivo original (pode estar aberto).");
        }
    }

}
