import persistance.dao.MovimentacaoDAO;
import model.entities.Movimentacao;

public class Main {
    public static void main(String[] args) {
//        Veiculo veiculoTeste = new Veiculo("SEM3T12","HONDA","CIVIC", true,"2014");
//
//        VeiculoDAO veiculoDAO = new VeiculoDAO();
//        veiculoDAO.SalvarVeiculo(veiculoTeste);

//        veiculoDAO.ExcluirVeiculo(4L);
//        veiculoDAO.ExcluirVeiculo(4L);
//        veiculoDAO.ExcluirVeiculo(4L);

        Movimentacao movimentacaoTeste = new Movimentacao(0L, 0L, "Multa", "20/12/2025", 300.00);
        MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
        //movimentacaoDAO.ExcluirMovimentacao(8L);
        //movimentacaoDAO.SalvarMovimentacao(movimentacaoTeste);

        System.out.println(movimentacaoDAO.lerMovimentacao(10L));
    }
}
