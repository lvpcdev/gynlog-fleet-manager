package util;

import exceptions.PersistenciaException;
import model.entities.Veiculo;
import service.MovimentacaoService.VeiculoCusto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    public static void exportarRelatorio10(List<VeiculoCusto> listaOrdenada, String caminhoArquivo) {
        if (listaOrdenada == null || listaOrdenada.isEmpty()) {
            throw new PersistenciaException("Não há dados para exportar.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Cabeçalho
            bw.write("Classificacao,ID,Placa,Marca,Modelo,Ano,Status,CustoTotal");
            bw.newLine();

            VeiculoCusto menor = listaOrdenada.get(0);
            VeiculoCusto maior = listaOrdenada.get(listaOrdenada.size() - 1);

            // Linha Maior Custo
            bw.write(formatarLinha("Maior Custo", maior));
            bw.newLine();

            // Linha Menor Custo
            bw.write(formatarLinha("Menor Custo", menor));
            bw.newLine();

            // Espaçador ou título para a lista completa
            bw.newLine();
            bw.write("Ranking Completo (Crescente)");
            bw.newLine();

            // Demais linhas ordenadas
            for (int i = 0; i < listaOrdenada.size(); i++) {
                bw.write(formatarLinha(String.valueOf(i + 1) + "º", listaOrdenada.get(i)));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new PersistenciaException("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    private static String formatarLinha(String classificacao, VeiculoCusto vc) {
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
}
