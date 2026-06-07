package util;

import exceptions.PersistenciaException;
import model.entities.Movimentacao;
import model.entities.TipoDespesa;
import model.entities.Veiculo;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvExporter {

    private static final String SEPARADOR = ";";
    private static final String ENCODING = "UTF-8";
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void exportarVeiculos(List<Veiculo> veiculos, String caminhoArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(caminhoArquivo), ENCODING))) {
            bw.write('\uFEFF');
            bw.write("ID;Placa;Marca;Modelo;Categoria;Ano;Status");
            bw.newLine();

            for (Veiculo v : veiculos) {
                bw.write(String.format("%d;%s;%s;%s;%s;%s;%s",
                        v.getId(),
                        escapeCsv(v.getPlaca()),
                        escapeCsv(v.getMarca()),
                        escapeCsv(v.getModelo()),
                        escapeCsv(v.getCategoria()),
                        v.getAnoDeFabricacao(),
                        v.getStatusVeiculo()
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar veículos: " + e.getMessage());
        }
    }

    public static void exportarMovimentacoes(List<Movimentacao> movimentacoes, String caminhoArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(caminhoArquivo), ENCODING))) {
            bw.write('\uFEFF');
            bw.write("ID;Data;Veiculo;Tipo;Descricao;Valor;Status");
            bw.newLine();

            for (Movimentacao m : movimentacoes) {
                String veiculoPlaca = (m.getVeiculo() != null && m.getVeiculo().getPlaca() != null) ? m.getVeiculo().getPlaca() : "";
                String tipoDespesaDescricao = (m.getTipoDespesa() != null && m.getTipoDespesa().getDescricao() != null) ? m.getTipoDespesa().getDescricao() : "";

                bw.write(String.format("%d;%s;%s;%s;%s;R$ %.2f;%s",
                        m.getId(),
                        m.getData().format(FORMATO_DATA),
                        escapeCsv(veiculoPlaca),
                        escapeCsv(tipoDespesaDescricao),
                        escapeCsv(m.getDescricao()),
                        m.getValor().doubleValue(),
                        m.getStatusMovimentacao()
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar movimentações: " + e.getMessage());
        }
    }

    public static void exportarTiposDespesa(List<TipoDespesa> tipos, String caminhoArquivo) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(caminhoArquivo), ENCODING))) {
            bw.write('\uFEFF');
            bw.write("ID;Descricao;Status");
            bw.newLine();

            for (TipoDespesa td : tipos) {
                bw.write(String.format("%d;%s;%s",
                        td.getId(),
                        escapeCsv(td.getDescricao()),
                        td.getStatusTipoDespesa()
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar tipos de despesa: " + e.getMessage());
        }
    }

    private static String escapeCsv(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(";") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}