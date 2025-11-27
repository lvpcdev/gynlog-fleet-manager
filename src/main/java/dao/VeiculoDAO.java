package dao;

import model.entities.Veiculo;

import java.io.*;
import java.time.format.DateTimeFormatter;

public class VeiculoDAO {
    Long idAtual = 0L;
    private final String caminho = "data/veiculos.txt";

    private DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("yyyy");

    public void salvar(Veiculo veiculo) {
        File arquivo = new File(caminho);
        veiculo.setIdVeiculo(gerarId());
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
            System.err.println("Erro ao salvar veiculo: " + e.getMessage());
        }


    }

    public Long gerarId(){
        File arquivoOriginal = new File("data/veiculos.txt");

        try (LineNumberReader lnr = new LineNumberReader(new FileReader(arquivoOriginal))){
            String linha = lnr.readLine();
            while(linha != null) {
                linha = lnr.readLine();
                if (linha== null) {
                    idAtual = (long)lnr.getLineNumber();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao processar: " + e.getMessage());
        }
        return idAtual;
    }

    public void AtualizarIds(){
        File arquivoOriginal = new File("data/veiculos.txt");
        File arquivoTemp = new File("data/veiculos-temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha = reader.readLine();
            Long idAnterior = -1L;

            while (linha != null) {
                String[] partes = linha.split(" \\| ");

                if(Long.parseLong(partes[0]) != idAnterior + 1 && Long.parseLong(partes[0]) != 0){
                    partes[0] = String.valueOf(idAnterior + 1);
                }

                idAnterior = Long.parseLong(partes[0]);
                linha = String.join(" | ", partes);

                bw.write(linha);
                bw.newLine();

                linha = reader.readLine();

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

    public void excluir(Long idParaExcluir) {
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
        AtualizarIds();
    }
}
