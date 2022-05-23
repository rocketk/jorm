package io.github.rocketk.jorm.mapper.row;

import io.github.rocketk.jorm.json.JsonMapper;
import io.github.rocketk.jorm.json.JsonMapperFactory;
import io.github.rocketk.jorm.json.JsonProvider;
import io.github.rocketk.jorm.mapper.column.ColumnFieldNameMapper;
import io.github.rocketk.jorm.mapper.column.DelimiterBasedStringArrayColumnFieldMapper;
import io.github.rocketk.jorm.mapper.column.SnakeCamelColumnFieldNameMapper;
import io.github.rocketk.jorm.mapper.column.StringArrayColumnFieldMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author pengyu
 * @date 2021/12/15
 */
public class DefaultRowMapperFactory implements RowMapperFactory {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int cacheMaxSize;
    private final long cacheDuration;
    private final TimeUnit cacheTimeUnit;
    private final Cache<Class, RowMapper> rowMapperCache;

    private final ColumnFieldNameMapper columnFieldNameMapper = new SnakeCamelColumnFieldNameMapper();
    private final StringArrayColumnFieldMapper stringArrayColumnFieldMapper;
    private final JsonMapper jsonMapper;

    public DefaultRowMapperFactory(String arrayDelimiter, JsonProvider jsonProvider) {
        this(arrayDelimiter, jsonProvider, 1000, 60, TimeUnit.MINUTES);
    }

    public DefaultRowMapperFactory(String arrayDelimiter, JsonProvider jsonProvider, int cacheMaxSize, long cacheDuration, TimeUnit cacheTimeUnit) {
        logger.info("invoking DefaultRowMapperFactory");
        if (StringUtils.isBlank(arrayDelimiter)) {
            arrayDelimiter = " ";
        }
        stringArrayColumnFieldMapper = new DelimiterBasedStringArrayColumnFieldMapper(arrayDelimiter);
        jsonMapper = JsonMapperFactory.getJsonMapper(jsonProvider);
        this.cacheMaxSize = cacheMaxSize;
        this.cacheDuration = cacheDuration;
        this.cacheTimeUnit = cacheTimeUnit;
        rowMapperCache = CacheBuilder.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterWrite(cacheDuration, cacheTimeUnit)
                .build(new CacheLoader<Class, RowMapper>() {
                    @Override
                    public RowMapper load(Class model) throws Exception {
                        return createRowMapper(model);
                    }
                });
    }

    private <T> RowMapper<T> createRowMapper(Class<T> model) {
        logger.info("creating rowMapper for {}", model.getCanonicalName());
        final DefaultRowMapper<T> rm = new DefaultRowMapper<>(columnFieldNameMapper, stringArrayColumnFieldMapper, jsonMapper, model);
        logger.info("created rowMapper for {}", model.getCanonicalName());
        return rm;
    }

    @Override
    public <T> RowMapper<T> getRowMapper(Class<T> model) {
//        final String modelCanonicalName = model.getCanonicalName();
        try {
            return rowMapperCache.get(model, () -> createRowMapper(model));
        } catch (ExecutionException e) {
            logger.error("failed to create RowMapper, caused by: {}", e.getMessage());
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
//        return this.rowMapperCache.computeIfAbsent(modelCanonicalName, k -> new DefaultRowMapper<>(
//                columnFieldNameMapper,
//                stringArrayColumnFieldMapper,
//                jsonMapper,
//                model
//        ));

//        return rowMapper;
//        if (this.rowMapperCache.get(modelCanonicalName) != null) {
//            return this.rowMapperCache.get(modelCanonicalName);
//        }
//        RowMapper<T> rm = new DefaultRowMapper<>(model);
//        this.rowMapperCache.put(modelCanonicalName, rm);
//        return rm;
    }
}
