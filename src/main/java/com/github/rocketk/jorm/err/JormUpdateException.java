package com.github.rocketk.jorm.err;

/**
 * @author pengyu
 * @date 2021/12/13
 */
public class JormUpdateException extends RuntimeException {
    public JormUpdateException() {
    }

    public JormUpdateException(String message) {
        super(message);
    }

    public JormUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public JormUpdateException(Throwable cause) {
        super(cause);
    }

    public JormUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
