package io.github.rocketk.jorm.listener.metrics;

import io.github.rocketk.jorm.listener.Listener;
import io.github.rocketk.jorm.listener.event.MutationStatementExecutedEvent;
import io.github.rocketk.jorm.listener.SqlMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public class MetricsMutationExecutedListener implements Listener<MutationStatementExecutedEvent> {
    private final MeterRegistry meterRegistry;
    private final SqlMapper sqlMapper;

    public MetricsMutationExecutedListener(MeterRegistry meterRegistry, SqlMapper sqlMapper) {
        this.meterRegistry = meterRegistry;
        this.sqlMapper = sqlMapper;
    }

    @Override
    public void onEvent(MutationStatementExecutedEvent event) {
        if (meterRegistry == null) {
            return;
        }
        Timer.builder("jorm.statement.execution")
                .tag("mode", Optional.ofNullable(event.getMutationMode()).map(Enum::name).orElse("Unknown"))
                .tag("sql", StringUtils.defaultIfBlank(sqlMapper.toSql(event.getSql(), event.getArgs()), "Unknown"))
                .tag("success", Boolean.toString(event.isSuccess()))
                .tag("exception", Optional.ofNullable(event.getException()).map(e -> e.getClass().getCanonicalName()).orElse("None"))
                .register(meterRegistry)
                .record(event.getCosts());
    }

}
