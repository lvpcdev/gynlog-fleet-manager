package dao;

import exceptions.ArquivoNaoEncontradoException;
import exceptions.PersistenciaException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import model.enums.StatusMovimentacao;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {


    private final String caminhoArquivo = "data/movimentacoes/movimentacoes.txt";
    private final String caminhoId = "data/movimentacoes/movimentacoesUltimoId.txt";

    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public void salvar(Movimentacao movimentacao) {
        File arquivo = new File(caminhoArquivo);
        movimentacao.setId(gerarId());
        Long idVeiculo = movimentacao.getVeiculo().getId();
        Long idTipoDespesa = movimentacao.getTipoDespesa().getId();

        String linha = movimentacao.getId() + " | "
                + idVeiculo + " | "
                + idTipoDespesa + " | "
                + movimentacao.getData().format(formatadorData) + " | "
                + movimentacao.getValor() .setScale(2, RoundingMode.HALF_UP) + " | "
                + movimentacao.getDescricao() + " | "
                + movimentacao.getStatusMovimentacao();


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo, true))) {
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar movimentação: ", e);
        }
    }


    public List<Movimentacao> listarTodos() {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        File arquivo = new File(caminhoArquivo);

        if (!arquivo.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo movimentações não encontrado.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");
                if (partes.length == 7) {
                    try {
                        Long idMovimentacao = Long.parseLong(partes[0]);
                        Long idVeiculo = Long.parseLong(partes[1]);
                        Long idTipoDespesa = Long.parseLong(partes[2]);
                        LocalDate data = LocalDate.parse(partes[3], formatadorData);
                        BigDecimal valor = new BigDecimal(partes[4]);
                        String descricao = partes[5];
                        StatusMovimentacao statusMovimentacao = StatusMovimentacao.valueOf(partes[6]);


                        Veiculo veiculo = new Veiculo();
                        veiculo.setId(idVeiculo);

                        TipoDespesa tipoDespesa = new TipoDespesa();
                        tipoDespesa.setId(idTipoDespesa);


                        Movimentacao movimentacao = new Movimentacao(veiculo, tipoDespesa, descricao, data, valor, statusMovimentacao);
                        movimentacao.setId(idMovimentacao);
                        movimentacoes.add(movimentacao);
                    } catch (NumberFormatException e) {
                        throw new PersistenciaException("Erro ao converter linha: " + linha, e);

                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao listar tipos de despesa", e);
        }
        return movimentacoes;
    }


    public void atualizar(Movimentacao movimentacao) {
        File arquivoOriginal = new File(caminhoArquivo);
        File arquivoTemp = new File("data/movimentacoes/movimentacoesTemp.txt");

        if (!arquivoOriginal.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo movimentações não encontrado.");
        }

        String movimentacaoTexto = movimentacao.getId() + " | " +
                movimentacao.getVeiculo().getId() + " | " +
                movimentacao.getTipoDespesa().getId() + " | " +
                movimentacao.getData().format(formatadorData) + " | " +
                movimentacao.getValor().setScale(2, RoundingMode.HALF_UP) + " | " +
                movimentacao.getDescricao() + " | " +
                movimentacao.getStatusMovimentacao();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");


                if (Long.parseLong(partes[0]) == movimentacao.getId()) {
                    linha = movimentacaoTexto;
                }

                bw.write(linha);
                bw.newLine();

            }
        } catch (IOException | NumberFormatException e) {
            throw new PersistenciaException("Erro ao atualizar movimentação", e);
        }


        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                throw new PersistenciaException("Erro ao renomear arquivo temporário");
            }
        } else {
            throw new PersistenciaException("Erro ao deletar arquivo original");
        }
    }


    public void excluir(Long id) {
        File arquivoOriginal = new File(caminhoArquivo);
        File arquivoTemp = new File("data/movimentacoes/movimentacoesTemp.txt");

        if (!arquivoOriginal.exists()) {
            throw new ArquivoNaoEncontradoException("Arquivo movimentações não encontrado.");
        }


        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(" \\| ");

                if (Long.parseLong(partes[0]) == id) {
                    continue;
                }

                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException | NumberFormatException e) {
            throw new PersistenciaException("Erro ao processar a exclusão da movimentação: ", e);
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
            throw new ArquivoNaoEncontradoException("Arquivo movimentações ultimo id não encontrado.");
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
