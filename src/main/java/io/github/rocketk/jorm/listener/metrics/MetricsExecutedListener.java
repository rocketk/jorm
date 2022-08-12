package io.github.rocketk.jorm.listener.metrics;

import io.github.rocketk.jorm.listener.Listener;
import io.github.rocketk.jorm.listener.SqlTagMapper;
import io.github.rocketk.jorm.listener.event.StatementExecutedEvent;
import io.github.rocketk.jorm.util.StringUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.Optional;

import static io.github.rocketk.jorm.util.StringUtils.emptyWrap;

/**
 * @author pengyu
 */
public class MetricsExecutedListener implements Listener<StatementExecutedEvent> {
    private final MeterRegistry meterRegistry;
    private final SqlTagMapper sqlTagMapper;

    public MetricsExecutedListener(MeterRegistry meterRegistry, SqlTagMapper sqlTagMapper) {
        this.meterRegistry = meterRegistry;
        this.sqlTagMapper = sqlTagMapper;
    }

    @Override
    public void onEvent(StatementExecutedEvent event) {
        if (meterRegistry == null) {
            return;
        }
        Timer.builder("jorm.statement.execution")
                .tag("instance_name", emptyWrap(event.getInstanceName()))
                .tag("operation_id", emptyWrap(event.getOperationId()))
                .tag("type", Optional.ofNullable(event.getStmtType()).map(Enum::name).orElse("Unknown"))
                .tag("sql", generateSqlTag(event.getSql(), event.getArgs()))
                .tag("success", Boolean.toString(event.isSuccess()))
                .tag("exception", Optional.ofNullable(event.getException()).map(e -> e.getClass().getCanonicalName()).orElse("None"))
                .register(meterRegistry).record(event.getCosts());
    }

    private String generateSqlTag(String rawSql, Object[] args) {
        return Optional.ofNullable(sqlTagMapper)
                .map(m -> m.toSql(rawSql, args))
                .map(StringUtils::escapeWhiteCharsUseRegex)
                .orElse("Unknown");
    }

}
