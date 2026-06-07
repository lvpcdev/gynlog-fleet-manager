package dao;

import exceptions.ArquivoNaoEncontradoException;
import exceptions.PersistenciaException;
import model.entities.TipoDespesa;
import model.enums.StatusTipoDespesa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDespesaDAO {

    private final String caminhoArquivo = "data/despesas/tiposDespesa.txt";
    private final String caminhoId = "data/despesas/tiposDespesaUltimoId.txt";


    public void salvar(TipoDespesa tipoDespesa) {
        File arquivo = new File(caminhoArquivo);

        tipoDespesa.setId(gerarId());
        String linha = tipoDespesa.getId() + " | "
                + tipoDespesa.getDescricao() + " | "
                + tipoDespesa.getStatusTipoDespesa();



        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar tipo de despesa: ", e);
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
                if (partes.length == 3) {

                    try {
                        Long id = Long.parseLong(partes[0].trim());
                        String descricao = partes[1].trim();
                        StatusTipoDespesa statusTipoDespesa = StatusTipoDespesa.valueOf(partes[2].trim());

                        TipoDespesa tipoDeDespesa = new TipoDespesa(descricao, statusTipoDespesa);
                        tipoDeDespesa.setId(id);
                        tiposDeDespesa.add(tipoDeDespesa);

                    } catch (NumberFormatException e) {
                        throw new PersistenciaException("Erro ao converter linha: " + linha, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao listar tipos de despesa", e);
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
                + tipoDespesa.getDescricao() + " | "
                + tipoDespesa.getStatusTipoDespesa();

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
            throw new PersistenciaException("Erro ao atualizar tipo de despesa", e);
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

