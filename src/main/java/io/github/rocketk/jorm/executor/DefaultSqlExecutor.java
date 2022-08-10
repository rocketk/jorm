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

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.List;

import static io.github.rocketk.jorm.listener.SilentListenerFactory.silentListener;

/**
 * @author pengyu
 */
public class DefaultSqlExecutor implements SqlExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final boolean enableTrace;
    private final Listener<StatementExecutedEvent> listener;

    public DefaultSqlExecutor() {
        this(false);
    }

    public DefaultSqlExecutor(boolean enableTrace) {
        this(enableTrace, null);
    }

    public DefaultSqlExecutor(boolean enableTrace, Listener<StatementExecutedEvent> listener) {
        this.enableTrace = enableTrace;
        this.listener = listener == null ? silentListener(StatementExecutedEvent.class) : listener;
    }


    @Override
    public <T> T executeQuery(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter, ResultSetReader<T> resultSetReader) {
        try (final Connection conn = ds.getConnection(); final PreparedStatement ps = conn.prepareStatement(sql)) {
            argsSetter.setArguments(ps, args);
            final EventEmitter<StatementExecutedEvent> emitter = newSqlExecutedEmitter(sql, args, StatementExecutedEvent.StmtType.QUERY);
            try (final ResultSet rs = ps.executeQuery()) {
                emitter.emit();
                return resultSetReader.read(rs);
            } catch (SQLException e) {
                emitter.emit(e);
                throw e;
            }
        } catch (SQLException e) {
            logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}", sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
            throw new JormQueryException(e);
        }
    }


    @Override
    public long[] executeUpdateAndReturnKeys(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter) {
        try (final Connection conn = ds.getConnection(); final PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            argsSetter.setArguments(ps, args);
            final EventEmitter<StatementExecutedEvent> emitter = newSqlExecutedEmitter(sql, args, StatementExecutedEvent.StmtType.MUTATION);
            try {
                ps.executeUpdate();
                emitter.emit();
            } catch (SQLException e) {
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
    public long executeUpdate(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter) {
        try (final Connection conn = ds.getConnection(); final PreparedStatement ps = conn.prepareStatement(sql)) {
            argsSetter.setArguments(ps, args);
            final EventEmitter<StatementExecutedEvent> emitter = newSqlExecutedEmitter(sql, args, StatementExecutedEvent.StmtType.MUTATION);
            try {
                final int affected = ps.executeUpdate();
                emitter.emit();
                return affected;
            } catch (SQLException e) {
                emitter.emit(e);
                throw e;
            }
        } catch (SQLException e) {
            throw new JormMutationException(e);
        }
    }

    private EventEmitter<StatementExecutedEvent> newSqlExecutedEmitter(String sql, Object[] args, StatementExecutedEvent.StmtType stmtType) {
        if (enableTrace) {
            final EventBuilder<StatementExecutedEvent> builder = EventBuilder.builder(StatementExecutedEvent.class)
                    .stmtType(stmtType)
                    .sql(sql)
                    .args(args)
                    .startedAt(new Date());
            return new EventEmitter<>(builder, listener);
        }
        return new EventEmitter<>();
    }
}
