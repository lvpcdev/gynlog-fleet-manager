package dao;

import model.entities.Movimentacao;
import model.entities.Veiculo;

import java.io.*;
import java.time.format.DateTimeFormatter;

public class VeiculoDAO {
    private final String caminho = "data/veiculos.txt";

    private DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("yyyy");

    public void salvar(Veiculo veiculo) {
        File arquivo = new File(caminho);

        String movimentacaoTexto = veiculo.getIdVeiculo() + " | "
                + veiculo.getPlaca() + " | "
                + veiculo.getMarca() + " | "
                + veiculo.getModelo() + " | "
                + veiculo.getAnoDeFabricacao().format(fmtData) + " | "
                + veiculo.isEstado();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))){
            writer.write(movimentacaoTexto);
            writer.newLine();

            System.out.println("Veiculo salvo com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar veiculo: " + e.getMessage());
        }


    }

    public Long gerarId(){
        File arquivoOriginal = new File("data/veiculos.txt");
        File arquivoTemp = new File("data/veiculos-temp.txt");

        try (LineNumberReader lnr = new LineNumberReader(new FileReader(arquivoOriginal));{

            String linha;

            while ((linha = lnr.readLine()) != null) {
                Long idAtual = (long)lnr.getLineNumber();

                String[] partes = linha.split(" | ");
                Long idExistente = Long.parseLong(partes[0]);


                if (idAtual.equals(idExistente)) {
                    gerarId()
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar exclusão: " + e.getMessage());
        }
    }

    public void excluir(Long idParaExcluir) {
        File arquivoOriginal = new File("data/veiculos.txt");
        File arquivoTemp = new File("data/veiculos-temp.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                String[] partes = linha.split(" | ");
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
