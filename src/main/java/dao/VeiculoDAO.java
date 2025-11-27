package dao;

import model.entities.Veiculo;

import java.io.*;
import java.time.format.DateTimeFormatter;

public class VeiculoDAO {
    Long idAtual = 0L;
    private final String caminho = "data/veiculos.txt";

    private DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("yyyy");

    public void SalvarVeiculo(Veiculo veiculo) {
        File arquivo = new File(caminho);
        veiculo.setIdVeiculo(GerarId());
        String veiculoTexto = veiculo.getIdVeiculo() + " | "
                + veiculo.getPlaca() + " | "
                + veiculo.getMarca() + " | "
                + veiculo.getModelo() + " | "
                + veiculo.getAnoDeFabricacao().format(fmtData) + " | "
                + veiculo.isEstado();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))){
            writer.write(veiculoTexto);
            writer.newLine();


            System.out.println("Veiculo salvo com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao SalvarVeiculo veiculo: " + e.getMessage());
        }

    }

    private Long GerarId(){
        File arquivoUltimoId = new File("data/VeiculosUltimoId.txt");
        File arquivoUltimoIdTemp = new File("data/VeiculosUltimoIdTemp.txt")
        Long ultimoId = 0L;
        try (BufferedReader readerUltimoId = new BufferedReader(new FileReader(arquivoUltimoId));
            BufferedWriter writerUltimodId = new BufferedWriter(new FileWriter(arquivoUltimoIdTemp))){

            String linha = readerUltimoId.readLine();

            if(linha == null){
                ultimoId = 0L;
                idAtual = ultimoId;
                writer.write(String.valueOf(ultimoId));
                return idAtual;
            }

            ultimoId = Long.parseLong(linha);
            ultimoId++;

            idAtual = ultimoId;

        } catch (IOException e) {
            System.err.println("Erro ao processar: " + e.getMessage());
        }
        return idAtual;
    }

    private void AtualizarVeiculos(Long idEscolhido){
        File arquivoOriginal = new File("data/veiculos.txt");
        File arquivoTemp = new File("data/veiculos-temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha = reader.readLine();

            while (linha != null) {
                String[] partes = linha.split(" \\| ");

                if(Long.parseLong(partes[0]) == idEscolhido){

                }

                linha = String.join(" | ", partes);
//
//                bw.write(linha);
//                bw.newLine();
//
//                linha = reader.readLine();

            }

        } catch (IOException e) {
            System.err.println("Erro ao atualiza lista: " + e.getMessage());
        }

        if (arquivoOriginal.delete()) {
            boolean sucesso = arquivoTemp.renameTo(arquivoOriginal);

            if (!sucesso) {
                System.err.println("Erro ao atualizar lista");
            }
        } else {
            System.err.println("Não foi possível apagar o arquivo original (pode estar aberto).");
        }
    }

    public void ExcluirVeiculo(Long idParaExcluir) {
        File arquivoOriginal = new File("data/veiculos.txt");
        File arquivoTemp = new File("data/veiculos-temp.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha = br.readLine();
            while (linha != null) {
                String[] partes = linha.split(" \\| ");
                Long idAtual = Long.parseLong(partes[0]);

                if (idAtual.equals(idParaExcluir)) {
                    linha = br.readLine();
                    continue;
                }


                bw.write(linha);
                bw.newLine();
                linha = br.readLine();
                if(linha == null){
                    System.err.println("id não encontrado");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar exclusão: " + e.getMessage());
        }


        if (arquivoOriginal.delete()) {
            boolean sucesso = arquivoTemp.renameTo(arquivoOriginal);

            if (!sucesso) {
                System.err.println("Erro ao atualizar a lista");
            }
        } else {
            System.err.println("Não foi possível apagar o arquivo original (pode estar aberto).");
        }
    }

}
