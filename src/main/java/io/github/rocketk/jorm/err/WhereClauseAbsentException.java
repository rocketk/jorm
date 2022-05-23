package io.github.rocketk.jorm.err;

/**
 * @author pengyu
 */
public class WhereClauseAbsentException extends JormMutationException {
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
