package io.github.rocketk.jorm.dialect;

/**
 * @author pengyu
 * @date 2022/4/23
 */
public class StandardLimitOffsetAppender implements LimitOffsetAppender {
    @Override
    public void appendLimitAndOffset(StringBuilder sql, Long limit, Long offset) {
        if (offset != null) {
            sql.append(" offset ").append(offset);
        }
        if (limit != null) {
            sql.append(" fetch first ").append(limit).append(" rows only");
        }
    }
}
