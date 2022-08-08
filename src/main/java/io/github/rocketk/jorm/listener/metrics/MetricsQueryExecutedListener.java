package io.github.rocketk.jorm.listener.metrics;

import io.github.rocketk.jorm.listener.Listener;
import io.github.rocketk.jorm.listener.event.QueryStatementExecutedEvent;
import io.github.rocketk.jorm.listener.SqlMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public class MetricsQueryExecutedListener implements Listener<QueryStatementExecutedEvent> {
    private final MeterRegistry meterRegistry;
    private final SqlMapper sqlMapper;

    public MetricsQueryExecutedListener(MeterRegistry meterRegistry, SqlMapper sqlMapper) {
        this.meterRegistry = meterRegistry;
        this.sqlMapper = sqlMapper;
    }

    @Override
    public void onEvent(QueryStatementExecutedEvent event) {
        if (meterRegistry == null) {
            return;
        }
        Timer.builder("jorm.statement.execution")
                .tag("mode", "QUERY")
                .tag("sql", StringUtils.defaultIfBlank(sqlMapper.toSql(event.getSql(), event.getArgs()), "Unknown"))
                .tag("success", Boolean.toString(event.isSuccess()))
                .tag("exception", Optional.ofNullable(event.getException()).map(e -> e.getClass().getCanonicalName()).orElse("None"))
                .register(meterRegistry)
                .record(event.getCosts());
    }

}
