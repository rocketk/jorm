package com.github.rocketk.jorm.err;

/**
 * @author pengyu
 * @date 2022/4/19
 */
public class WhereClauseAbsentException extends JormUpdateException {
    public WhereClauseAbsentException() {
    }

    public WhereClauseAbsentException(String message) {
        super(message);
    }

    public WhereClauseAbsentException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhereClauseAbsentException(Throwable cause) {
        super(cause);
    }

    public WhereClauseAbsentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
