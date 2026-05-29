package dao;

import exceptions.ArquivoNaoEncontradoException;
import model.entities.TipoDespesa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDespesaDAO {

    private final String caminhoArquivo = "data/despesas/tiposDespesa.txt";
    private final String caminhoId = "data/despesas/tiposDespesaUltimoId.txt";


    public void salvar(TipoDespesa tipoDespesa) {
        File arquivo = new File(caminhoArquivo);

        tipoDespesa.setId(gerarId());
        String linha = tipoDespesa.getId() + " | " + tipoDespesa.getDescricao();



        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(linha);
            bw.newLine();
            System.out.println("Tipo de despesa salva com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar tipo de despesa: " + e.getMessage());
        }
    }


    public List<TipoDespesa> listarTodos() {
        List<TipoDespesa> tiposDeDespesa = new ArrayList<>();
        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo tipos de despesa não encontrado.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }


                String[] partes = linha.split(" \\| ");
                if (partes.length == 2) {

                    try {
                        Long id = Long.parseLong(partes[0].trim());
                        String descricao = partes[1].trim();

                        TipoDespesa tipoDeDespesa = new TipoDespesa(descricao);
                        tipoDeDespesa.setId(id);
                        tiposDeDespesa.add(tipoDeDespesa);

                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao converter dados da linha: '" + linha + "'. Linha ignorada.");
                    }
                } else {
                    System.err.println("Aviso: Linha mal formatada no arquivo de tipo de despesa foi ignorada: '" + linha + "'");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao listar todos os tipos de despesa: " + e.getMessage());
        }
        return tiposDeDespesa;
    }


    public void atualizar(TipoDespesa tipoDespesa) {
        File arquivoOriginal = new File(caminhoArquivo);
        File arquivoTemp = new File("data/despesas/tiposDespesaTemp.txt");

        if (!arquivoOriginal.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo tipos de despesa não encontrado.");
        }

        String tipoDespesaTexto = tipoDespesa.getId() + " | "
                + tipoDespesa.getDescricao();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");


                if (Long.parseLong(partes[0]) == tipoDespesa.getId()) {
                    linha = tipoDespesaTexto;
                }


                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao atualizar tipo de despesa: " + e.getMessage());
            return;
        }

        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                System.err.println("Erro ao renomear arquivo temporário");
            }
        } else {
            System.err.println("Erro ao deletar arquivo original");
        }
    }


    public void excluir(Long id) {
        File arquivoOriginal = new File(caminhoArquivo);
        File arquivoTemp = new File("data/despesas/tiposDespesaTemp.txt");

        if (!arquivoOriginal.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo tipos de despesa não encontrado.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");

                if (Long.parseLong(partes[0]) == id) {
                    continue;
                }

                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao processar exclusão de tipo de despesa: " + e.getMessage());
            return;
        }


        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                System.err.println("Erro ao renomear arquivo temporário");
            }
        } else {
            System.err.println("Erro ao deletar arquivo original");
        }
    }

    private Long gerarId() {
        File arquivoUltimoId = new File(caminhoId);

        if (!arquivoUltimoId.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo tipos de despesa ultimo id não encontrado.");
        }

        Long novoId = 0L;
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoUltimoId))) {

            String linha = br.readLine();

            if (linha != null) {
                novoId = Long.parseLong(linha);
                novoId++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler ID: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoUltimoId))) {
            bw.write(String.valueOf(novoId));
        } catch (IOException e) {
            System.err.println("Erro ao salvar ID: " + e.getMessage());
        }
        return novoId;
    }
}

