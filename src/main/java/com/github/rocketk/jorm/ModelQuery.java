package com.github.rocketk.jorm;

import com.github.rocketk.jorm.mapper.row.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author pengyu
 * @date 2021/12/12
 */
public interface ModelQuery<T> {
//    ModelQuery<T> model(Class<T> model);

    ModelQuery<T> rowMapper(RowMapper<T> rowMapper);

    /**
     * 当columns非空时，将仅查询给定的这些columns
     * 如果你的数据表中的列比较多，而你有需要高频的查询少数几个列的时候，这个方法将会有效降低数据库的IO压力
     * @param columns
     * @return
     */
    ModelQuery<T> select(String... columns);

    ModelQuery<T> omit(String... columns);

    ModelQuery<T> table(String table);

    ModelQuery<T> where(String whereClause, Object... args);

    ModelQuery<T> orderBy(String orderByClause);

    ModelQuery<T> limit(int limit);

    ModelQuery<T> offset(int offset);

    ModelQuery<T> shouldFindDeleted(boolean findDeleted);

    Optional<T> first();

    List<T> find();

}
