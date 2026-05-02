package me.catst0day.Eclipse.Managers.Database;

public class EclipseSQLException extends RuntimeException {

    public EclipseSQLException(String message) {
        super(message);
    }

    public EclipseSQLException(String message, Throwable cause) {
        super(message, cause);
    }
}