package util;

import exceptions.PersistenciaException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;
import service.MovimentacaoService.VeiculoCusto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvExporter {

    // Formato de data para Movimentacao
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Método existente para o Relatório #10
    public static void exportarRelatorio10(List<VeiculoCusto> listaOrdenada, String caminhoArquivo) {
        if (listaOrdenada == null || listaOrdenada.isEmpty()) {
            throw new PersistenciaException("Não há dados para exportar.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Cabeçalho
            bw.write("Classificacao,ID,Placa,Marca,Modelo,Ano,Status,CustoTotal");
            bw.newLine();

            // Garante que há pelo menos um item para evitar IndexOutOfBounds
            if (!listaOrdenada.isEmpty()) {
                VeiculoCusto menor = listaOrdenada.get(0);
                // Se houver apenas um item, ele é o menor e o maior
                VeiculoCusto maior = listaOrdenada.size() > 1 ? listaOrdenada.get(listaOrdenada.size() - 1) : menor;

                // Linha Maior Custo
                bw.write(formatarLinhaRelatorio10("Maior Custo", maior));
                bw.newLine();

                // Linha Menor Custo
                bw.write(formatarLinhaRelatorio10("Menor Custo", menor));
                bw.newLine();
            }


            // Espaçador ou título para a lista completa
            bw.newLine();
            bw.write("Ranking Completo (Crescente)");
            bw.newLine();

            // Demais linhas ordenadas
            for (int i = 0; i < listaOrdenada.size(); i++) {
                bw.write(formatarLinhaRelatorio10(String.valueOf(i + 1) + "º", listaOrdenada.get(i)));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    private static String formatarLinhaRelatorio10(String classificacao, VeiculoCusto vc) {
        Veiculo v = vc.getVeiculo();
        return String.format("%s,%d,%s,%s,%s,%s,%s,%.2f",
                classificacao,
                v.getId(),
                v.getPlaca(),
                v.getMarca(),
                v.getModelo(),
                v.getAnoDeFabricacao(),
                v.getStatusVeiculo(),
                vc.getCustoTotal().doubleValue()
        );
    }

    // ADICIONAR EM CsvExporter
    public static void exportarVeiculosToCsv(List<Veiculo> veiculos, String caminhoArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Cabeçalho para Veículos
            bw.write("ID,Placa,Marca,Modelo,AnoDeFabricacao,StatusVeiculo");
            bw.newLine();

            for (Veiculo v : veiculos) {
                bw.write(String.format("%d,%s,%s,%s,%s,%s",
                        v.getId(),
                        escapeCsv(v.getPlaca()),
                        escapeCsv(v.getMarca()),
                        escapeCsv(v.getModelo()),
                        v.getAnoDeFabricacao(),
                        v.getStatusVeiculo()
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar veículos para CSV: " + e.getMessage());
        }
    }

    // ADICIONAR EM CsvExporter
    public static void exportarMovimentacoesToCsv(List<Movimentacao> movimentacoes, String caminhoArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Cabeçalho para Movimentações
            bw.write("ID,Descricao,Data,Valor,VeiculoID,VeiculoPlaca,TipoDespesaID,TipoDespesaDescricao");
            bw.newLine();

            for (Movimentacao m : movimentacoes) {
                String veiculoId = (m.getVeiculo() != null && m.getVeiculo().getId() != null) ? String.valueOf(m.getVeiculo().getId()) : "";
                String veiculoPlaca = (m.getVeiculo() != null && m.getVeiculo().getPlaca() != null) ? m.getVeiculo().getPlaca() : "";
                String tipoDespesaId = (m.getTipoDespesa() != null && m.getTipoDespesa().getId() != null) ? String.valueOf(m.getTipoDespesa().getId()) : "";
                String tipoDespesaDescricao = (m.getTipoDespesa() != null && m.getTipoDespesa().getDescricao() != null) ? m.getTipoDespesa().getDescricao() : "";

                bw.write(String.format("%d,%s,%s,%.2f,%s,%s,%s,%s",
                        m.getId(),
                        escapeCsv(m.getDescricao()), // Escapar vírgulas na descrição
                        m.getData().format(DATE_FORMATTER),
                        m.getValor().doubleValue(),
                        veiculoId,
                        escapeCsv(veiculoPlaca),
                        tipoDespesaId,
                        escapeCsv(tipoDespesaDescricao)
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar movimentações para CSV: " + e.getMessage());
        }
    }

    // ADICIONAR EM CsvExporter
    public static void exportarTipoDespesasToCsv(List<TipoDespesa> tipoDespesas, String caminhoArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Cabeçalho para Tipos de Despesa
            bw.write("ID,Descricao,StatusTipoDespesa");
            bw.newLine();

            for (TipoDespesa td : tipoDespesas) {
                bw.write(String.format("%d,%s,%s",
                        td.getId(),
                        escapeCsv(td.getDescricao()), // Escapar vírgulas na descrição
                        td.getStatusTipoDespesa()
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar tipos de despesa para CSV: " + e.getMessage());
        }
    }

    // ADICIONAR EM CsvExporter
    // Método auxiliar para escapar vírgulas e aspas duplas em campos CSV
    private static String escapeCsv(String field) {
        if (field == null) {
            return "";
        }
        // Se o campo contiver vírgula, aspas duplas ou quebra de linha, ele deve ser envolvido por aspas duplas.
        // Quaisquer aspas duplas dentro do campo devem ser duplicadas.
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
