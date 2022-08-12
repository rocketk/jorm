package io.github.rocketk.jorm.executor;

import com.google.common.collect.Lists;
import io.github.rocketk.jorm.err.JormMutationException;
import io.github.rocketk.jorm.err.JormQueryException;
import io.github.rocketk.jorm.listener.Listener;
import io.github.rocketk.jorm.listener.event.EventBuilder;
import io.github.rocketk.jorm.listener.event.EventEmitter;
import io.github.rocketk.jorm.listener.event.StatementExecutedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static io.github.rocketk.jorm.listener.SilentListenerFactory.silentListener;

/**
 * @author pengyu
 */
public class DefaultSqlExecutor implements SqlExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean enableEvent;
    private boolean enablePrintSql;
    private Duration lowQueryThreshold;
    private Listener<StatementExecutedEvent> listener = silentListener(StatementExecutedEvent.class);

    @Override
    public <T> T executeQuery(SqlRequest sqlRequest, ResultSetReader<T> resultSetReader) {
        final String sql = sqlRequest.getSql();
        final Object[] args = sqlRequest.getArgs();
        if (enablePrintSql) {
            logger.info("executing sql: \"{}\", args: \"{}\"", sql, args);
        }
        try (final Connection conn = sqlRequest.getDataSource().getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            sqlRequest.getArgsSetter().setArguments(ps, args);
            final EventEmitter<StatementExecutedEvent> emitter = newSqlExecutedEmitter(sqlRequest);
            final long start = System.currentTimeMillis();
            try (final ResultSet rs = ps.executeQuery()) {
                logLowQuery(start, sql, args);
                emitter.emit();
                return resultSetReader.read(rs);
            } catch (SQLException e) {
                logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}", sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
                emitter.emit(e);
                throw e;
            }
        } catch (SQLException e) {
            throw new JormQueryException(e);
        }
    }


    @Override
    public long[] executeUpdateAndReturnKeys(SqlRequest sqlRequest) {
        final String sql = sqlRequest.getSql();
        final Object[] args = sqlRequest.getArgs();
        if (enablePrintSql) {
            logger.info("executing sql: \"{}\", args: \"{}\"", sql, args);
        }
        try (final Connection conn = sqlRequest.getDataSource().getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sqlRequest.getArgsSetter().setArguments(ps, args);
            final EventEmitter<StatementExecutedEvent> emitter = newSqlExecutedEmitter(sqlRequest);
            final long start = System.currentTimeMillis();
            try {
                ps.executeUpdate();
                logLowQuery(start, sql, args);
                emitter.emit();
            } catch (SQLException e) {
                logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}", sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
                emitter.emit(e);
                throw e;
            }
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
    public long executeUpdate(SqlRequest sqlRequest) {
        final String sql = sqlRequest.getSql();
        final Object[] args = sqlRequest.getArgs();
        if (enablePrintSql) {
            logger.info("executing sql: \"{}\", args: \"{}\"", sql, args);
        }
        try (final Connection conn = sqlRequest.getDataSource().getConnection(); final PreparedStatement ps = conn.prepareStatement(sql)) {
            sqlRequest.getArgsSetter().setArguments(ps, args);
            final EventEmitter<StatementExecutedEvent> emitter = newSqlExecutedEmitter(sqlRequest);
            final long start = System.currentTimeMillis();
            try {
                final int affected = ps.executeUpdate();
                logLowQuery(start, sql, args);
                emitter.emit();
                return affected;
            } catch (SQLException e) {
                logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}", sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
                emitter.emit(e);
                throw e;
            }
        } catch (SQLException e) {
            throw new JormMutationException(e);
        }
    }

    private EventEmitter<StatementExecutedEvent> newSqlExecutedEmitter(SqlRequest sqlRequest) {
        if (enableEvent) {
            final EventBuilder<StatementExecutedEvent> builder = EventBuilder.builder(StatementExecutedEvent.class)
                    .instanceName(sqlRequest.getInstanceName())
                    .operationId(sqlRequest.getOperationId())
                    .stmtType(sqlRequest.getStmtType())
                    .sql(sqlRequest.getSql())
                    .args(sqlRequest.getArgs())
                    .startedAt(new Date());
            return new EventEmitter<>(builder, listener);
        }
        return new EventEmitter<>();
    }

    private void logLowQuery(long start, String sql, Object[] args) {
        if (lowQueryThreshold == null || lowQueryThreshold.isNegative()) {
            return;
        }
        final long costs = System.currentTimeMillis() - start;
        if (costs > lowQueryThreshold.toMillis()) {
            logger.warn("[JORM] LOW QUERY. sql: {}, args: {}, costs: {} ms", sql, args, costs);
        }
    }

    public void setEnableEvent(boolean enableEvent) {
        this.enableEvent = enableEvent;
    }

    public void setEnablePrintSql(boolean enablePrintSql) {
        this.enablePrintSql = enablePrintSql;
    }

    public void setLowQueryThreshold(Duration lowQueryThreshold) {
        this.lowQueryThreshold = lowQueryThreshold;
    }

    public void setListener(Listener<StatementExecutedEvent> listener) {
        this.listener = listener;
    }
}
