package exception;

public class CampoNaoCadastrado extends RuntimeException {
    public CampoNaoCadastrado(String message) {
        super(message);
    }
}
