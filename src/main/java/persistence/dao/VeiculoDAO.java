package persistence.dao;

import model.entities.Veiculo;
import model.enums.StatusVeiculo;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {
    Long idAtual = 0L;
    private final String caminho = "data/veiculos/veiculos.txt";

    private DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("yyyy");

    // CREATE
    public void SalvarVeiculo(Veiculo veiculo) {
        File arquivo = new File(caminho);
        veiculo.setIdVeiculo(GerarId());
        String veiculoTexto = veiculo.getIdVeiculo() + " | "
                + veiculo.getPlaca() + " | "
                + veiculo.getMarca() + " | "
                + veiculo.getModelo() + " | "
                + veiculo.getAnoDeFabricacao().format(fmtData) + " | "
                + veiculo.getStatusVeiculo();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))){
            writer.write(veiculoTexto);
            writer.newLine();


            System.out.println("Veiculo salvo com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao SalvarVeiculo veiculo: " + e.getMessage());
        }

    }

    // READ
    public String LerVeiculos(String dadoEscolhido, int linhaEscolhida){
        File arquivoOriginal = new File("data/veiculos/veiculos.txt");
        String linha = null;
        String[] partes = null;
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal))) {
            linha = br.readLine();
            partes = linha.split(" \\| ");
            for (int i = 1; i < linhaEscolhida; i++) {
                linha = br.readLine();
                partes = linha.split(" \\| ");
            }

            switch (dadoEscolhido) {
                case "idVeiculo":
                    return partes[0]; // retorna o Id

                case "placaVeiculo":
                    return partes[1]; // retorna a placa

                case "marcaVeiculo":
                    return partes[2]; // retorna a marca

                case "modeloVeiculo":
                    return partes[3]; // retorna o modelo

                case "anoDeFabricacao":
                    return partes[4]; // retorna o ano de fabricação

                case "estadoVeiculo":
                    return partes[5]; // retorna o estado(ativo ou inativo)
            }
        } catch (IOException e) {
            System.err.println("Erro ao processar exclusão: " + e.getMessage());
        }
        return null;
    }

    public List<Veiculo> listarTodos() {
        List<Veiculo> veiculos = new ArrayList<>();
        File arquivo = new File(caminho);

        if (!arquivo.exists()) {
            System.err.println("Aviso: Arquivo de veículos não encontrado. Retornando lista vazia.");
            return veiculos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");
                if (partes.length >= 6) {
                    try {
                        Long id = Long.parseLong(partes[0].trim());
                        String placa = partes[1].trim();
                        String marca = partes[2].trim();
                        String modelo = partes[3].trim();
                        String ano = partes[4].trim();
                        StatusVeiculo statusVeiculo = null;
                        statusVeiculo = statusVeiculo.setStatus(partes[5].trim());

                        Veiculo veiculo = new Veiculo(placa, marca, modelo, statusVeiculo, ano);
                        veiculo.setIdVeiculo(id);
                        veiculos.add(veiculo);

                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao converter dados da linha: '" + linha + "'. Linha ignorada.");
                    }
                } else {
                    System.err.println("Aviso: Linha mal formatada no arquivo de veículos foi ignorada: '" + linha + "'");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro de leitura no arquivo de veículos: " + e.getMessage());
        }

        return veiculos;
    }

    // UPDATE
    public void EditarVeiculo(Veiculo veiculo){
        File arquivoOriginal = new File(caminho);
        File arquivoTemp = new File("data/veiculos/veiculos-temp.txt");

        String veiculoTexto = veiculo.getIdVeiculo() + " | "
                + veiculo.getPlaca() + " | "
                + veiculo.getMarca() + " | "
                + veiculo.getModelo() + " | "
                + veiculo.getAnoDeFabricacao().format(fmtData) + " | "
                + veiculo.getStatusVeiculo();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha = reader.readLine();

            while (linha != null) {
                String[] partes = linha.split(" \\| ");

                if(Long.parseLong(partes[0]) == veiculo.getIdVeiculo()){
                    partes = veiculoTexto.split(" \\| ");
                    linha = String.join(" | ", partes);
                }

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

    // DELETE
    public void ExcluirVeiculo(Long idParaExcluir) {
        File arquivoOriginal = new File("data/veiculos/veiculos.txt");
        File arquivoTemp = new File("data/veiculos/veiculos-temp.txt");

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

    public int getQuantidadeDeVeiculos(){
        File arquivoOriginal = new File(caminho);
        int quantidade = 0;
        try (LineNumberReader reader = new LineNumberReader(new FileReader(arquivoOriginal))) {

            String linha = reader.readLine();

            while (linha != null) {
                quantidade++;
                linha = reader.readLine();
                if(linha == null){
                    break;
                }
            }


        } catch (IOException e) {
            System.err.println("Erro ao atualiza lista: " + e.getMessage());
        }
        return quantidade;
    }

    private Long GerarId(){
        File arquivoUltimoId = new File("data/veiculos/VeiculosUltimoId.txt");
        File arquivoUltimoIdTemp = new File("data/veiculos/VeiculosUltimoIdTemp.txt");
        Long ultimoId = 0L;
        try (BufferedReader readerUltimoId = new BufferedReader(new FileReader(arquivoUltimoId));
             BufferedWriter writerUltimodId = new BufferedWriter(new FileWriter(arquivoUltimoIdTemp))){

            String linha = readerUltimoId.readLine();

            if(linha == null){
                ultimoId = 0L;
            } else {
                ultimoId = Long.parseLong(linha);
                ultimoId++;
            }

            idAtual = ultimoId;
            writerUltimodId.write(String.valueOf(ultimoId));

        } catch (IOException e) {
            System.err.println("Erro ao processar: " + e.getMessage());
        }

        if (arquivoUltimoId.delete()) {
            boolean sucesso = arquivoUltimoIdTemp.renameTo(arquivoUltimoId);

            if (!sucesso) {
                System.err.println("Erro ao atualizar o id");
            }
        } else {
            System.err.println("Não foi possível apagar o arquivo original(GerarId)");
        }
        return idAtual;
    }


}