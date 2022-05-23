package io.github.rocketk.jorm;

import io.github.rocketk.jorm.dialect.Dialect;
import io.github.rocketk.jorm.mapper.row.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author pengyu
 */
public interface Query<T> {

    Query<T> rowMapper(RowMapper<T> rowMapper);

    /**
     * 当columns非空时，将仅查询给定的这些columns
     * 如果你的数据表中的列比较多，而你有需要高频的查询少数几个列的时候，这个方法将会有效降低数据库的IO压力
     *
     * @param columns
     * @return
     */
    Query<T> select(String... columns);

    Query<T> omit(String... columns);

    Query<T> table(String table);

    Query<T> where(String whereClause, Object... args);

    Query<T> rawSql(String rawSql, Object... args);

    Query<T> orderBy(String orderByClause);

    Query<T> limit(long limit);

    Query<T> offset(long offset);

    Query<T> dialect(Dialect dialect);

    /**
     * 只有当 model 有指定 JormTable 注解，并且 enableSoftDelete() == true 时，此字段才起作用。
     *
     * @param findDeleted 是否应当查询已删除的行
     * @return
     */
    Query<T> shouldFindDeletedRows(boolean findDeleted);

    Optional<T> first();

    List<T> find();

    long count();
}
