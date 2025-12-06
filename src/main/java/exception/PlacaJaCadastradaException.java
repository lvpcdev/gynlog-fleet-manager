package exception;

public class PlacaJaCadastradaException extends RuntimeException {
    public PlacaJaCadastradaException(String placa) {
        super("ERRO DE CADASTRO! \n Placa já cadastrada em um veículos existente: " + placa);
    }
}
