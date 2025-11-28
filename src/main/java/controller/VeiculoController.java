package controller;

import persistence.dao.VeiculoDAO;

import javax.swing.table.DefaultTableModel;

public class VeiculoController {
        int linhaAtual = 1;
    public void AtualizarVeiculos(DefaultTableModel tableModel) {
        VeiculoDAO veiculoDAO = new VeiculoDAO();
        if(veiculoDAO.getQuantidadeDeVeiculos() == 0){
            return;
        }
        while(linhaAtual <= veiculoDAO.getQuantidadeDeVeiculos()){
            tableModel.addRow(new Object[] {
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
