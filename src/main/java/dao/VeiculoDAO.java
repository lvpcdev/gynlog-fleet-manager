package dao;

import exceptions.ArquivoNaoEncontradoException;
import exceptions.PersistenciaException;
import model.entities.Veiculo;
import model.enums.StatusVeiculo;

import java.io.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {
    private final String caminhoArquivo = "data/veiculos/veiculos.txt";
    private final String caminhoId = "data/veiculos/veiculosUltimoId.txt";

    public void salvar(Veiculo veiculo) {
        File arquivo = new File(caminhoArquivo);
        veiculo.setId(gerarId());
        String linha = veiculo.getId() + " | "
                + veiculo.getPlaca() + " | "
                + veiculo.getMarca() + " | "
                + veiculo.getModelo() + " | "
                + veiculo.getAnoDeFabricacao() + " | "
                + veiculo.getStatusVeiculo();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar veiculo: ", e);
        }

    }

    public List<Veiculo> listarTodos() {
        List<Veiculo> veiculos = new ArrayList<>();
        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo veiculos não encontrado.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");
                if (partes.length == 6) {
                    try {
                        Long id = Long.parseLong(partes[0].trim());
                        String placa = partes[1].trim();
                        String marca = partes[2].trim();
                        String modelo = partes[3].trim();
                        Year ano = Year.parse(partes[4].trim());
                        StatusVeiculo statusVeiculo = StatusVeiculo.valueOf(partes[5].trim());

                        Veiculo veiculo = new Veiculo(placa, marca, modelo, statusVeiculo, ano);
                        veiculo.setId(id);
                        veiculos.add(veiculo);

                    } catch (NumberFormatException e) {
                        throw new PersistenciaException("Erro ao converter linha: " + linha, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao listar veiculos", e);
        }

        return veiculos;
    }

    public void atualizar(Veiculo veiculo) {
        File arquivoOriginal = new File(caminhoArquivo);
        File arquivoTemp = new File("data/veiculos/veiculosTemp.txt");

        if (!arquivoOriginal.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo veiculos não encontrado.");
        }

        String veiculoTexto = veiculo.getId() + " | "
                + veiculo.getPlaca() + " | "
                + veiculo.getMarca() + " | "
                + veiculo.getModelo() + " | "
                + veiculo.getAnoDeFabricacao() + " | "
                + veiculo.getStatusVeiculo();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");

                if (Long.parseLong(partes[0]) == veiculo.getId()) {
                    linha = veiculoTexto;
                }

                bw.write(linha);
                bw.newLine();


            }

        } catch (IOException e) {
            throw new PersistenciaException("Erro ao atualizar veiculo", e);
        }


        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                throw new PersistenciaException("Erro ao renomear arquivo temporário");
            }
        } else {
            throw new PersistenciaException("Erro ao deletar arquivo original");
        }
    }

    private Long gerarId() {
        File arquivoUltimoId = new File(caminhoId);

        if (!arquivoUltimoId.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo veiculos ultimo id não encontrado.");
        }

        Long novoId = 0L;
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoUltimoId))) {

            String linha = br.readLine();

            if (linha != null) {
                novoId = Long.parseLong(linha);
                novoId++;
            }

        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler ID", e);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoUltimoId))) {
            bw.write(String.valueOf(novoId));
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar ID", e);
        }
        return novoId;
    }

}