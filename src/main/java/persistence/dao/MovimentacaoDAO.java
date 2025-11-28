package persistence.dao;

import model.entities.Movimentacao;

import java.io.*;
import java.time.format.DateTimeFormatter;

public class MovimentacaoDAO {

    private final String caminho = "data/movimentacoes.txt";
    private final String idCaminho = "data/movimentacao_id_sequence.txt";
    private final DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void SalvarMovimentacao(Movimentacao mov) {
        File diretorio = new File("data");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        File arquivo = new File(caminho);

        if (mov.getIdMovimentacao() == null) {
            mov.setIdMovimentacao(GerarId());
        }

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
        long ultimoId = -1L;
        File idFile = new File(idCaminho);

        if (idFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(idFile))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    ultimoId = Long.parseLong(line.trim());
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Aviso: Não foi possível ler o arquivo de sequência de ID. Recalculando...");
                ultimoId = -1L;
            }
        }

        if (ultimoId == -1L) {
            File arquivoDados = new File(caminho);
            if (arquivoDados.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(arquivoDados))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        if (linha.trim().isEmpty()) continue;
                        String[] partes = linha.split(" \\| ");
                        if (partes.length > 0) {
                            try {
                                long idAtual = Long.parseLong(partes[0].trim());
                                if (idAtual > ultimoId) {
                                    ultimoId = idAtual;
                                }
                            } catch (NumberFormatException e) { /* Ignora */ }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao ler arquivo para inicializar ID: " + e.getMessage());
                }
            }
        }

        long proximoId = ultimoId + 1;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(idFile, false))) {
            writer.write(String.valueOf(proximoId));
        } catch (IOException e) {
            System.err.println("Erro crítico: Não foi possível atualizar o arquivo de sequência de ID: " + e.getMessage());
        }

        return proximoId;
    }

    public Movimentacao lerMovimentacao(Long idParaLer) {
        File arquivo = new File(caminho);

        if (!arquivo.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(" \\| ");
                Long idAtual = Long.parseLong(partes[0].trim());

                if (idAtual.equals(idParaLer)) {
                    Long idMovimentacao = Long.parseLong(partes[0].trim());
                    Long idVeiculo = Long.parseLong(partes[1].trim());
                    Long idTipoDespesa = Long.parseLong(partes[2].trim());
                    String descricao = partes[3].trim();
                    String data = partes[4].trim();
                    Double valor = Double.parseDouble(partes[5].trim());

                    return new Movimentacao( idVeiculo, idTipoDespesa, descricao, data, valor);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao ler a movimentação: " + e.getMessage());
        }

        return null;
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
