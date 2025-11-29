package controller;

import persistence.dao.VeiculoDAO;

import javax.swing.table.DefaultTableModel;

public class VeiculoController {
    public void AtualizarVeiculos(DefaultTableModel tableModel) {
        int linhaAtual = 1;
        VeiculoDAO veiculoDAO = new VeiculoDAO();
        if (veiculoDAO.getQuantidadeDeVeiculos() == 0) {
            return;
        }
        if (tableModel.getRowCount() == 0) {
            while (linhaAtual <= veiculoDAO.getQuantidadeDeVeiculos()) {
                tableModel.addRow(new Object[]{
                        veiculoDAO.LerVeiculos("idVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("placaVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("marcaVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("modeloVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("anoDeFabricacao", linhaAtual),
                        veiculoDAO.LerVeiculos("estadoVeiculo", linhaAtual)
                });
                linhaAtual++;
            }
        } else {
            int quantidadeDeLinhas = tableModel.getRowCount();
            for (int i = 0; i < quantidadeDeLinhas; i++) {
                    tableModel.removeRow(0);
            }
            while (linhaAtual <= veiculoDAO.getQuantidadeDeVeiculos()) {
                tableModel.addRow(new Object[]{
                        veiculoDAO.LerVeiculos("idVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("placaVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("marcaVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("modeloVeiculo", linhaAtual),
                        veiculoDAO.LerVeiculos("anoDeFabricacao", linhaAtual),
                        veiculoDAO.LerVeiculos("estadoVeiculo", linhaAtual)
                });
                linhaAtual++;
            }
        }
    }
}
