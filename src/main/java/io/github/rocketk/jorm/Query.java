package io.github.rocketk.jorm;

import io.github.rocketk.jorm.anno.JormTable;
import io.github.rocketk.jorm.dialect.Dialect;
import io.github.rocketk.jorm.mapper.row.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author pengyu
 */
public interface Query<T> {

    /**
     * Specify the custom implementation of {@link RowMapper}.
     *
     * @param rowMapper the custom implementation of {@link RowMapper}.
     * @return the instance of Query
     */
    Query<T> rowMapper(RowMapper<T> rowMapper);

    /**
     * If there are many columns in your table, but only a few columns are frequently used in most query cases,
     * this method will effectively reduce the IO pressure of the database.
     * <p>
     * If the columns are not empty, then only the given columns will be queried.
     * <p>
     * Example:
     * <pre>{@code
     *   query.select("name", "age");
     * }</pre>
     *
     * @param columns the column names which are expected returned from the database. null means all columns.
     * @return the instance of Query
     */
    Query<T> select(String... columns);

    /**
     * Specify the column names that should not be mapped from JDBC to the field of the target object.
     * <p>
     * However, it should be noted that if `omit()` is used without using `select()`,
     * the column clause in SQL will still be `*`, which means that JDBC will still return all values of all columns.
     * But JORM will omit the mapping of columns specified in `omit()` arguments.
     * <p>
     * Example:
     * <pre>{@code
     *   query.omit("password");
     * }</pre>
     *
     * @param columns the column names which are omitted for parsing.
     * @return the instance of Query
     */
    Query<T> omit(String... columns);

    /**
     * Specify the table name.
     * If the table name is not empy, JORM will ignore {@link JormTable#name()}.
     *
     * @param table the table name
     * @return the instance of Query
     */
    Query<T> table(String table);

    /**
     * Specify the where clause
     *
     * @param whereClause the where clause
     * @param args        the arguments of where clause
     * @return the instance of Query
     */
    Query<T> where(String whereClause, Object... args);

    Query<T> rawSql(String rawSql, Object... args);

    Query<T> orderBy(String orderByClause);

    Query<T> limit(long limit);

    Query<T> offset(long offset);

    Query<T> dialect(Dialect dialect);

    /**
     * This method only works when the model has the specified annotation {@link JormTable}
     * with {@code enableSoftDelete() == true}.
     *
     * @param findDeleted true if you want the deleted rows to be returned but you have added annotation
     *                    {@link JormTable} with {@code enableSoftDelete() == true}
     * @return the instance of Query
     */
    Query<T> shouldFindDeletedRows(boolean findDeleted);

    Optional<T> first();

    List<T> find();

    long count();
}
