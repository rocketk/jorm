package io.github.rocketk.jorm.dialect;

/**
 * @author pengyu
 */
public class LimitOffsetAppenderFactory {
    public static LimitOffsetAppender byDialect(Dialect dialect) {
        if (dialect == null) {
            return new StandardLimitOffsetAppender();
        }
        switch (dialect) {
            case STANDARD:
                return new StandardLimitOffsetAppender();
            case MYSQL:
                return new MysqlLimitOffsetAppender();
            case DERBY:
                return new DerbyLimitOffsetAppender();
            default:
                throw new IllegalArgumentException("Unknown Dialect: " + dialect);
        }
    }
}
