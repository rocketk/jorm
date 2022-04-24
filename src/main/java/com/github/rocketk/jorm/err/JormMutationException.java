package com.github.rocketk.jorm.err;

/**
 * @author pengyu
 * @date 2021/12/13
 */
public class JormMutationException extends RuntimeException {
    public JormMutationException() {
    }

    public JormMutationException(String message) {
        super(message);
    }

    public JormMutationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JormMutationException(Throwable cause) {
        super(cause);
    }

    public JormMutationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
