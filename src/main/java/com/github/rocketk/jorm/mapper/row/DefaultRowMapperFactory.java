package com.github.rocketk.jorm.mapper.row;

import com.github.rocketk.jorm.json.JsonMapper;
import com.github.rocketk.jorm.json.JsonMapperFactory;
import com.github.rocketk.jorm.mapper.column.ColumnFieldNameMapper;
import com.github.rocketk.jorm.mapper.column.DelimiterBasedStringArrayColumnFieldMapper;
import com.github.rocketk.jorm.mapper.column.SnakeCamelColumnFieldNameMapper;
import com.github.rocketk.jorm.mapper.column.StringArrayColumnFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author pengyu
 * @date 2021/12/15
 */
public class DefaultRowMapperFactory implements RowMapperFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, RowMapper> rowMapperCache = new ConcurrentHashMap<>();

    private final ColumnFieldNameMapper columnFieldNameMapper = new SnakeCamelColumnFieldNameMapper();
    private final StringArrayColumnFieldMapper stringArrayColumnFieldMapper;
    private final JsonMapper jsonMapper;

    public DefaultRowMapperFactory(String arrayDelimiter, String jsonProvider) {
        if (arrayDelimiter == null || arrayDelimiter.isEmpty()) {
            arrayDelimiter = " ";
        }
        stringArrayColumnFieldMapper = new DelimiterBasedStringArrayColumnFieldMapper(arrayDelimiter);
        jsonMapper = JsonMapperFactory.getJsonMapper(jsonProvider);
    }

    @Override
    public <T> RowMapper<T> createRowMapper(Class<T> model) {
        final String modelCanonicalName = model.getCanonicalName();
        return this.rowMapperCache.computeIfAbsent(modelCanonicalName, k -> new DefaultRowMapper<>(
                columnFieldNameMapper,
                stringArrayColumnFieldMapper,
                jsonMapper,
                model
        ));
//        return rowMapper;
//        if (this.rowMapperCache.get(modelCanonicalName) != null) {
//            return this.rowMapperCache.get(modelCanonicalName);
//        }
//        RowMapper<T> rm = new DefaultRowMapper<>(model);
//        this.rowMapperCache.put(modelCanonicalName, rm);
//        return rm;
    }
}
