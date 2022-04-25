package com.github.rocketk.jorm.err;

/**
 * @author pengyu
 * @date 2021/12/13
 */
public class JormTransactionException extends RuntimeException {
    public JormTransactionException() {
    }

    public JormTransactionException(String message) {
        super(message);
    }

    public JormTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JormTransactionException(Throwable cause) {
        super(cause);
    }

    public JormTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
