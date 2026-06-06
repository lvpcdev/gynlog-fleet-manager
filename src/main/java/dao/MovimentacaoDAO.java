package dao;

import exceptions.ArquivoNaoEncontradoException;
import exceptions.PersistenciaException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

public class MovimentacaoDAO {


    private final String caminhoArquivo = "data/movimentacoes/movimentacoes.txt";
    private final String caminhoId = "data/movimentacoes/movimentacoesUltimoId.txt";

    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // NOVO: Dependência para TipoDespesaDAO para buscar descrições
    private TipoDespesaDAO tipoDespesaDAO = new TipoDespesaDAO();


    public void salvar(Movimentacao movimentacao) {
        File arquivo = new File(caminhoArquivo);
        movimentacao.setId(gerarId());
        Long idVeiculo = movimentacao.getVeiculo().getId();
        Long idTipoDespesa = movimentacao.getTipoDespesa().getId();

        // NOVO: Adicionar quilometragemAtual à linha
        String linha = movimentacao.getId() + " | "
                + idVeiculo + " | "
                + idTipoDespesa + " | "
                + movimentacao.getData().format(formatadorData) + " | "
                + movimentacao.getValor() + " | "
                + movimentacao.getDescricao() + " | "
                + movimentacao.getQuilometragemAtual();


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
            // Se o arquivo não existe, retorna uma lista vazia em vez de lançar exceção
            // Isso é útil para o primeiro uso do sistema
            return movimentacoes;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");
                // NOVO: Ajustar para 6 ou 7 partes (com ou sem quilometragem)
                if (partes.length >= 6) {
                    try {
                        Long idMovimentacao = Long.parseLong(partes[0]);
                        Long idVeiculo = Long.parseLong(partes[1]);
                        Long idTipoDespesa = Long.parseLong(partes[2]);
                        LocalDate data = LocalDate.parse(partes[3], formatadorData);
                        BigDecimal valor = new BigDecimal(partes[4]);
                        String descricao = partes[5];
                        double quilometragemAtual = 0.0; // Valor padrão para compatibilidade

                        if (partes.length == 7) { // Se a quilometragem está presente
                            quilometragemAtual = Double.parseDouble(partes[6]);
                        }


                        Veiculo veiculo = new Veiculo();
                        veiculo.setId(idVeiculo);

                        TipoDespesa tipoDespesa = new TipoDespesa();
                        tipoDespesa.setId(idTipoDespesa);


                        // NOVO: Usar o construtor com quilometragem
                        Movimentacao movimentacao = new Movimentacao(veiculo, tipoDespesa, descricao, data, valor, quilometragemAtual);
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

        // NOVO: Adicionar quilometragemAtual à linha de atualização
        String movimentacaoTexto = movimentacao.getId() + " | " +
                movimentacao.getVeiculo().getId() + " | " +
                movimentacao.getTipoDespesa().getId() + " | " +
                movimentacao.getData().format(formatadorData) + " | " +
                movimentacao.getValor() + " | " +
                movimentacao.getDescricao() + " | " +
                movimentacao.getQuilometragemAtual();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linha.split(" \\| ");

                // NOVO: Verificar se a linha tem pelo menos 6 partes antes de tentar parsear o ID
                if (partes.length >= 6 && Long.parseLong(partes[0]) == movimentacao.getId()) {
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

                // NOVO: Verificar se a linha tem pelo menos 6 partes antes de tentar parsear o ID
                if (partes.length >= 6 && Long.parseLong(partes[0]) == id) {
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

        // NOVO: Criar o arquivo se não existir e inicializar com 0
        if (!arquivoUltimoId.exists()) {
            try {
                arquivoUltimoId.getParentFile().mkdirs(); // Garante que o diretório exista
                arquivoUltimoId.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoUltimoId))) {
                    bw.write("0");
                }
            } catch (IOException e) {
                throw new PersistenciaException("Erro ao criar arquivo de ID: ", e);
            }
        }


        Long novoId = 0L;
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoUltimoId))) {

            String linha = br.readLine();

            if (linha != null && !linha.trim().isEmpty()) { // NOVO: Verificar se a linha não está vazia
                novoId = Long.parseLong(linha);
                novoId++;
            } else {
                novoId = 1L; // Se o arquivo estava vazio ou só tinha espaços, começa com 1
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

    // NOVO: Método para buscar a última quilometragem registrada para um veículo
    public double buscarUltimaQuilometragemVeiculo(Long veiculoId) {
        return buscarUltimaQuilometragemVeiculoInterno(veiculoId, null);
    }

    // NOVO: Método para buscar a última quilometragem registrada para um veículo, excluindo uma movimentação específica
    public double buscarUltimaQuilometragemVeiculoExcluindoAtual(Long veiculoId, Long movimentacaoId) {
        return buscarUltimaQuilometragemVeiculoInterno(veiculoId, movimentacaoId);
    }

    private double buscarUltimaQuilometragemVeiculoInterno(Long veiculoId, Long movimentacaoIdExcluir) {
        List<Movimentacao> todasMovimentacoes = listarTodos();
        List<TipoDespesa> todosTiposDespesa = tipoDespesaDAO.listarTodos(); // Buscar todos os tipos de despesa

        // Mapear IDs de TipoDespesa para suas descrições para fácil acesso
        // Isso é necessário porque MovimentacaoDAO não tem acesso direto ao serviço de TipoDespesa
        // e o Movimentacao.tipoDespesa no DAO só tem o ID.
        Map<Long, String> tipoDespesaMap = todosTiposDespesa.stream()
                .collect(Collectors.toMap(TipoDespesa::getId, TipoDespesa::getDescricao));

        Optional<Movimentacao> ultimaMovimentacao = todasMovimentacoes.stream()
                .filter(mov -> mov.getVeiculo() != null && mov.getVeiculo().getId().equals(veiculoId))
                .filter(mov -> movimentacaoIdExcluir == null || !mov.getId().equals(movimentacaoIdExcluir)) // Excluir movimentação se ID for fornecido
                .filter(mov -> {
                    String descricaoTipo = tipoDespesaMap.get(mov.getTipoDespesa().getId());
                    return "COMBUSTÍVEL".equalsIgnoreCase(descricaoTipo);
                })
                .filter(mov -> mov.getQuilometragemAtual() > 0) // Apenas movimentações com quilometragem válida
                .sorted(Comparator
                        .comparing(Movimentacao::getData)
                        .thenComparing(Movimentacao::getQuilometragemAtual, Comparator.reverseOrder())) // Ordena por data e depois por quilometragem (decrescente)
                .reduce((first, second) -> second); // Pega o último elemento após a ordenação

        return ultimaMovimentacao.map(Movimentacao::getQuilometragemAtual).orElse(0.0);
    }

    // NOVO: Método para verificar se um veículo possui movimentações de combustível
    public boolean hasCombustivelMovimentacao(Long veiculoId) {
        List<Movimentacao> todasMovimentacoes = listarTodos();
        List<TipoDespesa> todosTiposDespesa = tipoDespesaDAO.listarTodos();

        Map<Long, String> tipoDespesaMap = todosTiposDespesa.stream()
                .collect(Collectors.toMap(TipoDespesa::getId, TipoDespesa::getDescricao));

        return todasMovimentacoes.stream()
                .filter(mov -> mov.getVeiculo() != null && mov.getVeiculo().getId().equals(veiculoId))
                .filter(mov -> {
                    String descricaoTipo = tipoDespesaMap.get(mov.getTipoDespesa().getId());
                    return "COMBUSTÍVEL".equalsIgnoreCase(descricaoTipo);
                })
                .findAny() // Encontra qualquer um que satisfaça os filtros
                .isPresent(); // Retorna true se encontrou, false caso contrário
    }
}
