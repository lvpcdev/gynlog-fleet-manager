package exceptionss;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message, Throwable causa) {
        super(message, causa);
    }

    public PersistenciaException(String message) {
        super(message);
    }
}
