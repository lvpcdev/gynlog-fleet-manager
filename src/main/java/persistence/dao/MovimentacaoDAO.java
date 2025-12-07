package persistence.dao;

import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import model.enums.StatusVeiculo;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {


    private final String CAMINHO_MOVIMENTACOES = "data/movimentacoes/movimentacoes.txt";
    private final String CAMINHO_ID_MOVIMENTACOES = "data/movimentacoes/MovimentacoesUltimoId.txt";

    private final String CAMINHO_VEICULOS = "data/veiculos/veiculos.txt";
    private final String CAMINHO_TIPOS_DESPESA = "data/despesas/tipos_despesa.txt";

    private final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    public void salvar(Movimentacao movimentacao) {
        try {
            movimentacao.setIdMovimentacao(gerarId());

            Long idVeiculo = movimentacao.getVeiculo().getIdVeiculo();
            Long idTipoDespesa = movimentacao.getTipoDespesa().getIdTipoDespesa();

            String linha = movimentacao.getIdMovimentacao() + " | " +
                    idVeiculo + " | " +
                    idTipoDespesa + " | " +
                    movimentacao.getData().format(FORMATADOR_DATA) + " | " +
                    movimentacao.getValor() + " | " +
                    movimentacao.getDescricao();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_MOVIMENTACOES, true))) {
                writer.write(linha);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Erro ao salvar movimentação: " + e.getMessage());
        }
    }


    public List<Movimentacao> listarTodos() {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        File arquivo = new File(CAMINHO_MOVIMENTACOES);

        if (!arquivo.exists()) {
            return movimentacoes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(" \\| ");
                if (partes.length == 6) {
                    Long idMovimentacao = Long.parseLong(partes[0]);
                    Long idVeiculo = Long.parseLong(partes[1]);
                    Long idTipoDespesa = Long.parseLong(partes[2]);
                    LocalDate data = LocalDate.parse(partes[3], FORMATADOR_DATA);
                    BigDecimal valor = new BigDecimal(partes[4]);
                    String descricao = partes[5];


                    Veiculo veiculo = buscarVeiculoPorId(idVeiculo);
                    TipoDespesa tipoDespesa = buscarTipoDespesaPorId(idTipoDespesa);

                    if (veiculo != null && tipoDespesa != null) {
                        Movimentacao mov = new Movimentacao(veiculo, tipoDespesa, descricao, data, valor);
                        mov.setIdMovimentacao(idMovimentacao);
                        movimentacoes.add(mov);
                    } else {
                        System.err.println("Aviso: Movimentação " + idMovimentacao + " ignorada. Dependência não encontrada (Veículo ou Tipo de Despesa).");
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao listar movimentações: " + e.getMessage());
        }
        return movimentacoes;
    }


    public void atualizar(Movimentacao movimentacao) {
        File arquivoOriginal = new File(CAMINHO_MOVIMENTACOES);
        File arquivoTemp = new File(CAMINHO_MOVIMENTACOES + ".tmp");


        String novaLinha = movimentacao.getIdMovimentacao() + " | " +
                movimentacao.getVeiculo().getIdVeiculo() + " | " +
                movimentacao.getTipoDespesa().getIdTipoDespesa() + " | " +
                movimentacao.getData().format(FORMATADOR_DATA) + " | " +
                movimentacao.getValor() + " | " +
                movimentacao.getDescricao();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linhaAtual;
            while ((linhaAtual = reader.readLine()) != null) {
                String[] partes = linhaAtual.split(" \\| ");

                Long idDaLinha = Long.parseLong(partes[0].trim());


                if (idDaLinha.equals(movimentacao.getIdMovimentacao())) {
                    writer.write(novaLinha);
                    writer.newLine();
                } else {

                    writer.write(linhaAtual);
                    writer.newLine();
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao atualizar movimentação: " + e.getMessage());
            return;
        }


        if (arquivoOriginal.delete()) {
            if (!arquivoTemp.renameTo(arquivoOriginal)) {
                System.err.println("Erro ao renomear arquivo temporário para o original (Movimentacao).");
            }
        } else {
            System.err.println("Erro ao deletar arquivo original (Movimentacao).");
        }
    }


    public void excluir(Long idParaExcluir) {
        File arquivoOriginal = new File(CAMINHO_MOVIMENTACOES);
        File arquivoTemp = new File(CAMINHO_MOVIMENTACOES + ".tmp");

        if (!arquivoOriginal.exists()) {
            System.err.println("Erro ao excluir: Arquivo de movimentações não encontrado.");
            return;
        }

        boolean encontrou = false;


        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(" \\| ");


                if (partes.length > 0 && !partes[0].trim().isEmpty()) {
                    Long idAtual = Long.parseLong(partes[0].trim());


                    if (idAtual.equals(idParaExcluir)) {
                        encontrou = true;
                        continue;
                    }
                }


                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao processar a exclusão da movimentação: " + e.getMessage());

            return;
        }


        if (!encontrou) {
            System.err.println("Aviso: Movimentação com ID " + idParaExcluir + " não encontrada para exclusão.");
            arquivoTemp.delete();
            return;
        }



        if (!arquivoOriginal.delete()) {
            System.err.println("Erro crítico: Não foi possível deletar o arquivo original. A exclusão falhou.");
            return;
        }

        if (!arquivoTemp.renameTo(arquivoOriginal)) {
            System.err.println("Erro crítico: Não foi possível renomear o arquivo temporário. Os dados podem estar em '" + arquivoTemp.getName() + "'.");
        }
    }


    private Veiculo buscarVeiculoPorId(Long idBusca) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CAMINHO_VEICULOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(" \\| ");
                if (partes.length >= 6) {
                    Long idAtual = Long.parseLong(partes[0].trim());
                    if (idAtual.equals(idBusca)) {
                        String placa = partes[1].trim();
                        String marca = partes[2].trim();
                        String modelo = partes[3].trim();
                        String ano = partes[4].trim();
                        StatusVeiculo statusVeiculo = StatusVeiculo.INATIVO;
                        statusVeiculo = statusVeiculo.setStatus(partes[5].trim());

                        Veiculo veiculo = new Veiculo(placa, marca, modelo, statusVeiculo, ano);
                        veiculo.setIdVeiculo(idAtual);
                        return veiculo;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao buscar veículo por ID (" + idBusca + "): " + e.getMessage());
        }
        return null;
    }

    private TipoDespesa buscarTipoDespesaPorId(Long idBusca) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CAMINHO_TIPOS_DESPESA))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(" \\| ");
                if (partes.length == 2) {
                    Long idAtual = Long.parseLong(partes[0].trim());
                    if (idAtual.equals(idBusca)) {
                        String descricao = partes[1].trim();
                        TipoDespesa td = new TipoDespesa(descricao);
                        td.setIdTipoDespesa(idAtual);
                        return td;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao buscar tipo de despesa por ID (" + idBusca + "): " + e.getMessage());
        }
        return null;
    }


    private Long gerarId() throws IOException {
        long proximoId = 1L;
        File arquivoId = new File(CAMINHO_ID_MOVIMENTACOES);

        if (!arquivoId.getParentFile().exists()) {
            arquivoId.getParentFile().mkdirs();
        }

        if (arquivoId.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivoId))) {
                String linha = reader.readLine();
                if (linha != null && !linha.trim().isEmpty()) {
                    proximoId = Long.parseLong(linha) + 1;
                }
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoId, false))) {
            writer.write(String.valueOf(proximoId));
        }
        return proximoId;
    }

    public Movimentacao buscarPorId(Long idBusca) {
        List<Movimentacao> todas = listarTodos();
        for (Movimentacao mov : todas) {
            if (mov.getIdMovimentacao().equals(idBusca)) {
                return mov;
            }
        }
        return null;
    }
}
