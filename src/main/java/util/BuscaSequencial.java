package util;

import model.entities.Veiculo;

import java.util.ArrayList;
import java.util.List;

public class BuscaSequencial {

    public static List<Veiculo> buscarPorPlaca (List<Veiculo> veiculos, String placa) {
        List<Veiculo> resultado = new ArrayList<>();

        for (int i = 0; i < veiculos.size(); i++) {
            if (veiculos.get(i).getPlaca().toUpperCase().contains(placa.toUpperCase())) {
                resultado.add(veiculos.get(i));
            }
        }

        return resultado;
    }

    public static List<Veiculo> buscarPorModelo(List<Veiculo> veiculos, String modelo) {
        List<Veiculo> resultado = new ArrayList<>();

        for (int i = 0; i < veiculos.size(); i++) {
            if(veiculos.get(i).getModelo().toUpperCase().contains(modelo.toUpperCase())) {
                resultado.add(veiculos.get(i));
            }
        }
        return resultado;
    }
}
