package util;

import model.entities.Movimentacao;
import model.entities.Veiculo;

import java.math.BigDecimal;
import java.util.List;

public class SelectionSort {

    public static List<Veiculo> ordenarVeiculosPorTotalDespesa(List<Veiculo> veiculos, List<Movimentacao> movimentacoes) {
        for (int i = 0; i < veiculos.size() - 1; i++) {
            int indiceMaior = i;

            for (int j = i + 1; j < veiculos.size(); j++) {
                BigDecimal totalJ = calcularTotalVeiculo(veiculos.get(j), movimentacoes);
                BigDecimal totalMaior = calcularTotalVeiculo(veiculos.get(indiceMaior), movimentacoes);

                if (totalJ.compareTo(totalMaior) > 0) {
                    indiceMaior = j;
                }
            }

            if (indiceMaior != i) {
                Veiculo temp = veiculos.get(i);
                veiculos.set(i, veiculos.get(indiceMaior));
                veiculos.set(indiceMaior, temp);
            }
        }

        return veiculos;
    }

    private static BigDecimal calcularTotalVeiculo(Veiculo veiculo, List<Movimentacao> movimentacoes) {
        BigDecimal total = BigDecimal.ZERO;
        for (Movimentacao mov : movimentacoes) {
            if (mov.getVeiculo().getId().equals(veiculo.getId())) {
                total = total.add(mov.getValor());
            }
        }
        return total;
    }
}
