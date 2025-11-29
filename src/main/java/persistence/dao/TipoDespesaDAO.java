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

    }

    // DELETE
    public void excluir(Long idParaExcluir) {
        File arquivoOriginal = new File("data/despesas/tipos_despesa.txt");
        File arquivoTemp = new File("data/despesas/tipos_despesa-temp.txt");

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

