import dao.VeiculoDAO;
import model.entities.Veiculo;

public class Main {
    public static void main(String[] args) {
        Veiculo veiculoTeste = new Veiculo("SEM3T12","HONDA","CIVIC", true,"2016");

        VeiculoDAO veiculoDAO = new VeiculoDAO();
        veiculoDAO.SalvarVeiculo(veiculoTeste);

        veiculoDAO.ExcluirVeiculo(4L);
//        veiculoDAO.ExcluirVeiculo(4L);
//        veiculoDAO.ExcluirVeiculo(4L);

    }
}
