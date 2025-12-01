package persistence.dao;

import model.entities.TipoDespesa;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDespesaDAO {

    private final String CAMINHO_ARQUIVO = "data/despesas/tipos_despesa.txt";
    private final String CAMINHO_ID = "data/despesas/TiposDespesaUltimoId.txt";

    // CREATE
    public void salvar(TipoDespesa tipoDespesa) {
        try {
            tipoDespesa.setIdTipoDespesa(gerarId());
            String linha = tipoDespesa.getIdTipoDespesa() + " | " + tipoDespesa.getDescricao();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO, true))) {
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            // Tratamento de Exceção
            System.err.println("Erro ao salvar tipo de despesa: " + e.getMessage());
        }
    }

    // READ
    public List<TipoDespesa> listarTodos() {
        List<TipoDespesa> tiposDeDespesa = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CAMINHO_ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(" \\| ");
                if (partes.length == 2) {
                    Long id = Long.parseLong(partes[0]);
                    String descricao = partes[1];

                    TipoDespesa td = new TipoDespesa(descricao);
                    td.setIdTipoDespesa(id);
                    tiposDeDespesa.add(td);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao listar tipos de despesa: " + e.getMessage());
        }
        return tiposDeDespesa;
    }

    // UPDATE
    public void atualizar(TipoDespesa tipoDespesa) {
        File arquivoOriginal = new File(CAMINHO_ARQUIVO);
        File arquivoTemp = new File(CAMINHO_ARQUIVO + ".tmp");


        String linhaAtualizada = tipoDespesa.getIdTipoDespesa() + " | " + tipoDespesa.getDescricao();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linhaSendoLida;
            while ((linhaSendoLida = reader.readLine()) != null) {
                if (linhaSendoLida.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linhaSendoLida.split(" \\| ");


                if (partes.length >= 2 && Long.parseLong(partes[0].trim()) == tipoDespesa.getIdTipoDespesa()) {
                    linhaSendoLida = linhaAtualizada;
                }


                writer.write(linhaSendoLida);
                writer.newLine();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao atualizar tipo de despesa: " + e.getMessage());
            return;
        }

        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                System.err.println("Erro ao renomear arquivo temporário para o original (TipoDespesa).");
            }
        } else {
            System.err.println("Erro ao deletar arquivo original (TipoDespesa). Pode estar em uso.");
        }
    }

    // DELETE
    public void excluir(Long idParaExcluir) {
        File arquivoOriginal = new File(CAMINHO_ARQUIVO);
        File arquivoTemp = new File(CAMINHO_ARQUIVO + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linhaAtual;
            while ((linhaAtual = reader.readLine()) != null) {
                if (linhaAtual.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linhaAtual.split(" \\| ");


                if (partes.length >= 2) {
                    Long idDaLinha = Long.parseLong(partes[0].trim());


                    if (idDaLinha.equals(idParaExcluir)) {
                        continue;
                    }
                }


                writer.write(linhaAtual);
                writer.newLine();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao processar exclusão de tipo de despesa: " + e.getMessage());
            return;
        }


        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                System.err.println("Erro ao renomear arquivo temporário para o original (TipoDespesa).");
            }
        } else {
            System.err.println("Erro ao deletar arquivo original (TipoDespesa).");
        }
    }

    private Long gerarId() throws IOException {

        long proximoId = 1L;
        File arquivoId = new File(CAMINHO_ID);
        if (arquivoId.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivoId))) {
                String linha = reader.readLine();
                if (linha != null) {
                    proximoId = Long.parseLong(linha) + 1;
                }
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoId))) {
            writer.write(String.valueOf(proximoId));
        }
        return proximoId;
    }
}

