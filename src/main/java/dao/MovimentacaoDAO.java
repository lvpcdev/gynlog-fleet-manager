package dao;

import model.entities.Movimentacao;

import java.io.*;
import java.time.format.DateTimeFormatter;

public class MovimentacaoDAO {

    private final String caminho = "data/movimentacoes.txt";
    private final DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void SalvarMovimentacao(Movimentacao mov) {
        File diretorio = new File("data");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
        
        File arquivo = new File(caminho);

        mov.setIdMovimentacao(GerarId());
        String movimentacaoTexto = mov.getIdMovimentacao() + " | "
                + mov.getIdVeiculo() + " | "
                + mov.getIdTipoDespesa() + " | "
                + mov.getDescricao() + " | "
                + mov.getData().format(fmtData) + " | "
                + mov.getValor();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))) {
            writer.write(movimentacaoTexto);
            writer.newLine();

            System.out.println("Movimentação salva com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar movimentação: " + e.getMessage());
        }
    }

    private Long GerarId() {
        File arquivo = new File(caminho);
        long maxId = -1L;

        if (!arquivo.exists()) {
            return 0L;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(" \\| ");
                if (partes.length > 0) {
                    try {
                        long idAtual = Long.parseLong(partes[0].trim());
                        if (idAtual > maxId) {
                            maxId = idAtual;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Aviso: linha mal formatada no arquivo de movimentações: " + linha);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de movimentações para gerar ID: " + e.getMessage());
        }

        return maxId + 1;
    }

    public void ExcluirMovimentacao(Long idParaExcluir) {
        File arquivoOriginal = new File(caminho);
        File arquivoTemp = new File("data/movimentacoes-temp.txt");
        boolean idEncontrado = false;

        if (!arquivoOriginal.exists()) {
            System.err.println("Arquivo de movimentações não encontrado.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(" \\| ");
                Long idAtualLinha = Long.parseLong(partes[0].trim());

                if (idAtualLinha.equals(idParaExcluir)) {
                    idEncontrado = true;
                    continue;
                }
                bw.write(linha);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar exclusão: " + e.getMessage());
            return;
        }

        if (!idEncontrado) {
            System.err.println("ID " + idParaExcluir + " não encontrado para exclusão.");
            arquivoTemp.delete();
            return;
        }

        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                System.err.println("Erro ao renomear o arquivo temporário.");
            } else {
                System.out.println("Movimentação com ID " + idParaExcluir + " excluída com sucesso.");
            }
        } else {
            System.err.println("Não foi possível apagar o arquivo original.");
        }
    }
}
