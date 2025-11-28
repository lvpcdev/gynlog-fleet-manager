import model.entities.Movimentacao;

public class Main {
    public static void main(String[] args) {
        Movimentacao teste = new Movimentacao(1L, 1L, 1L, "Multa", "20/11/2025", 350.00);

        //MovimentacaoDAO dao = new MovimentacaoDAO();
        //dao.salvar(teste);
        //dao.excluir(1L);
    }
}
