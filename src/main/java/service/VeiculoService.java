package service;

import dao.VeiculoDAO;
import exceptions.EntidadeNaoEncontradaException;
import exceptions.ValidacaoException;
import model.entities.Veiculo;
import model.enums.StatusVeiculo;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class VeiculoService {
    private VeiculoDAO veiculoDAO = new VeiculoDAO();

    public void salvar(Veiculo veiculo) {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().isEmpty()) {
            throw new ValidacaoException("Placa obrigatória");
        }

        if (veiculo.getAnoDeFabricacao() == null) {
            throw new ValidacaoException("Ano obrigatório");
        }

        if (veiculo.getStatusVeiculo() == null) {
            throw new ValidacaoException("Status obrigátorio");
        }

        if (veiculo.getMarca() == null || veiculo.getMarca().isEmpty()) {
            throw new ValidacaoException("Marca obrigatória");
        }

        if (veiculo.getModelo() == null || veiculo.getModelo().isEmpty()) {
            throw new ValidacaoException("Modelo obrigatório");
        }

        if (veiculo.getCategoria() == null || veiculo.getCategoria().isEmpty()) {
            throw new ValidacaoException("Categoria obrigatória");
        }


        Year anoAtual = Year.now();
        Year anoVeiculo = veiculo.getAnoDeFabricacao();
        if (anoVeiculo.isBefore(Year.of(1950)) || anoVeiculo.isAfter(anoAtual)) {
            throw new ValidacaoException("Ano inválido");
        }

        if (!placaValida(veiculo.getPlaca())) {
            throw new ValidacaoException("Placa inválida. Use o formato ABC1234 ou ABC1D23");
        }

        if (existePlaca(veiculo.getPlaca())) {
            throw new ValidacaoException("Placa já cadastrada");
        }

        veiculoDAO.salvar(veiculo);
    }

    public void atualizar(Veiculo veiculo) {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().isEmpty()) {
            throw new ValidacaoException("Placa obrigatória");
        }

        if (veiculo.getAnoDeFabricacao() == null) {
            throw new ValidacaoException("Ano obrigatório");
        }

        if (veiculo.getStatusVeiculo() == null) {
            throw new ValidacaoException("Status obrigátorio");
        }

        if (veiculo.getMarca() == null || veiculo.getMarca().isEmpty()) {
            throw new ValidacaoException("Marca obrigatória");
        }

        if (veiculo.getModelo() == null || veiculo.getModelo().isEmpty()) {
            throw new ValidacaoException("Modelo obrigatório");
        }

        if (veiculo.getCategoria() == null || veiculo.getCategoria().isEmpty()) {
            throw new ValidacaoException("Categoria obrigatória");
        }

        Year anoAtual = Year.now();
        Year anoVeiculo = veiculo.getAnoDeFabricacao();
        if (anoVeiculo.isBefore(Year.of(1950)) || anoVeiculo.isAfter(anoAtual)) {
            throw new ValidacaoException("Ano inválido");
        }

        if (!placaValida(veiculo.getPlaca())) {
            throw new ValidacaoException("Placa inválida. Use o formato ABC1234 ou ABC1D23");
        }

        if (existePlacaParaOutroVeiculo(veiculo.getPlaca(), veiculo.getId())) {
            throw new ValidacaoException("Placa já cadastrada");
        }

        veiculoDAO.atualizar(veiculo);
    }

    public List<Veiculo> listarTodos() {
        return veiculoDAO.listarTodos();
    }

    public List<Veiculo> listarInativos() {
        List<Veiculo> veiculos = listarTodos();
        List<Veiculo> veiculosInativos = new ArrayList<>();

        for (Veiculo v : veiculos) {
            if (v.getStatusVeiculo().equals(StatusVeiculo.INATIVO)) {
                veiculosInativos.add(v);
            }
        }
        return veiculosInativos;
    }

    public List<Veiculo> listarAtivos() {
        List<Veiculo> veiculos = listarTodos();
        List<Veiculo> veiculosAtivos = new ArrayList<>();

        for (Veiculo v : veiculos) {
            if (v.getStatusVeiculo().equals(StatusVeiculo.ATIVO)) {
                veiculosAtivos.add(v);
            }
        }
        return veiculosAtivos;
    }

    public Veiculo buscarPorId(Long id) {
        List<Veiculo> veiculos = listarTodos();

        for (Veiculo v : veiculos) {
            if (v.getId().equals(id)) {
                return v;
            }
        }

        throw new EntidadeNaoEncontradaException("Veiculo com id " + id + " não encontrado");
    }



    private boolean existePlaca(String placa) {
        List<Veiculo> veiculos = listarTodos();
        for (Veiculo v : veiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return true;
            }
        }
        return false;
    }

    private boolean existePlacaParaOutroVeiculo(String placa, Long id) {
        List<Veiculo> veiculos = listarTodos();

        for (Veiculo v : veiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa) && !v.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private boolean placaValida(String placa) {
        String formatoAntigo = "[A-Z]{3}[0-9]{4}";
        String formatoMercosul = "[A-Z]{3}[0-9][A-Z][0-9]{2}";
        return placa.matches(formatoAntigo) || placa.matches(formatoMercosul);
    }
}
