import dao.MovimentacaoDAO;
import dao.VeiculoDAO;
import model.entities.Movimentacao;
import model.entities.Veiculo;

public class Main {
    public static void main(String[] args) {
        Veiculo veiculoTeste = new Veiculo("SEM3T12","HONDA","CIVIC", true,"2006");
        Veiculo veiculoTeste2 = new Veiculo("SEM3T12","HONDA","CIVIC", true,"2007");

        VeiculoDAO VeiculoDAO = new VeiculoDAO();
        VeiculoDAO.salvar(veiculoTeste);
        VeiculoDAO.salvar(veiculoTeste2);

//        VeiculoDAO.excluir(1L);
    }
}
