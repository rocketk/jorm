package io.github.rocketk.jorm.dialect;

/**
 * @author pengyu
 * @date 2022/4/23
 */
public class MysqlLimitOffsetAppender implements LimitOffsetAppender {
    @Override
    public void appendLimitAndOffset(StringBuilder sql, Long limit, Long offset) {
        if (limit != null) {
            sql.append(" limit ").append(limit).append(" ");
        }
        if (offset != null) {
            sql.append(" offset ").append(offset).append(" ");
        }
    }
}
