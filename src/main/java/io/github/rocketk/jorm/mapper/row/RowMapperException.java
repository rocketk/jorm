package io.github.rocketk.jorm.mapper.row;

/**
 * @author pengyu
 */
public class RowMapperException extends RuntimeException {
    public RowMapperException() {
    }

    public RowMapperException(String message) {
        super(message);
    }

    public RowMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public RowMapperException(Throwable cause) {
        super(cause);
    }

    public RowMapperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
