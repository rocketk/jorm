package io.github.rocketk.jorm.mapper.column;

/**
 * @author pengyu
 */
public class CannotParseColumnToFieldException extends RuntimeException {
    public CannotParseColumnToFieldException() {
    }

    public CannotParseColumnToFieldException(String message) {
        super(message);
    }

    public CannotParseColumnToFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotParseColumnToFieldException(Throwable cause) {
        super(cause);
    }

    public CannotParseColumnToFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
