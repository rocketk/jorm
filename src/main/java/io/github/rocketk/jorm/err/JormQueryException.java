package io.github.rocketk.jorm.err;

/**
 * @author pengyu
 */
public class JormQueryException extends RuntimeException {
    public JormQueryException() {
    }

    public JormQueryException(String message) {
        super(message);
    }

    public JormQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public JormQueryException(Throwable cause) {
        super(cause);
    }

    public JormQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
