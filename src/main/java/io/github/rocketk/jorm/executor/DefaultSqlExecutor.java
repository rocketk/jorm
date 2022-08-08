package io.github.rocketk.jorm.executor;

import com.google.common.collect.Lists;
import io.github.rocketk.jorm.err.JormMutationException;
import io.github.rocketk.jorm.err.JormQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * @author pengyu
 * @date 2022/8/8
 */
public class DefaultSqlExecutor<T> implements SqlExecutor<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public T executeQuery(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter, ResultSetReader<T> resultSetReader) {
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            argsSetter.setArguments(ps, args);
            try (final ResultSet rs = ps.executeQuery()) {
                return resultSetReader.read(rs);
            } catch (SQLException e) {
                logger.error("failed to parse the result set to the model, sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}",
                        sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
                throw e;
            }
        } catch (SQLException e) {
            logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}",
                    sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
            throw new JormQueryException(e);
        }
    }

    @Override
    public long[] executeUpdateAndReturnKeys(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter) {
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            argsSetter.setArguments(ps, args);
            ps.executeUpdate();
            try (final ResultSet generatedKeys = ps.getGeneratedKeys()) {
                List<Long> keys = Lists.newArrayList();
                while (generatedKeys.next()) {
                    keys.add(generatedKeys.getLong(1));
                }
                return keys.stream().mapToLong(Long::longValue).toArray();
            }
        } catch (SQLException e) {
            throw new JormMutationException(e);
        }
    }

    @Override
    public long executeUpdate(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter) {
        try (final Connection conn = ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            argsSetter.setArguments(ps, args);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new JormMutationException(e);
        }
    }
}
