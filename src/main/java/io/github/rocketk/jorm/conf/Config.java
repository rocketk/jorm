package io.github.rocketk.jorm.conf;

import io.github.rocketk.jorm.dialect.Dialect;
import io.github.rocketk.jorm.dialect.LimitOffsetAppender;
import io.github.rocketk.jorm.json.JsonProvider;
import io.github.rocketk.jorm.listener.SqlTagMapper;
import io.micrometer.core.instrument.MeterRegistry;

import static io.github.rocketk.jorm.json.JsonProvider.JACKSON;

/**
 * @author pengyu
 */
public class Config {
//    public static final String JACKSON = "jackson";
//    public static final String GSON = "gson";
//    public static final String FASTJSON = "fastjson";
    /**
     * 数组分隔符，用来将数据库列中的字符串值分隔成Java中的字符串数组对象
     */
    private String arrayDelimiter = " ";
    /**
     * 使用何种JSON序列化器
     * jackson, gson, fastjson
     */
    private JsonProvider jsonProvider = JACKSON;
    private LimitOffsetAppender limitOffsetAppender;
    private boolean printSql = true;
    private Dialect dialect = Dialect.STANDARD;
    private MeterRegistry meterRegistry;
    private SqlTagMapper sqlTagMapper;


    public String getArrayDelimiter() {
        return arrayDelimiter;
    }

    public void setArrayDelimiter(String arrayDelimiter) {
        this.arrayDelimiter = arrayDelimiter;
    }

    public JsonProvider getJsonProvider() {
        return jsonProvider;
    }

    public void setJsonProvider(JsonProvider jsonProvider) {
        this.jsonProvider = jsonProvider;
    }

    public boolean isPrintSql() {
        return printSql;
    }

    public void setPrintSql(boolean printSql) {
        this.printSql = printSql;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public LimitOffsetAppender getLimitOffsetAppender() {
        return limitOffsetAppender;
    }

    public void setLimitOffsetAppender(LimitOffsetAppender limitOffsetAppender) {
        this.limitOffsetAppender = limitOffsetAppender;
    }

    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public SqlTagMapper getSqlTagMapper() {
        return sqlTagMapper;
    }

    public void setSqlTagMapper(SqlTagMapper sqlTagMapper) {
        this.sqlTagMapper = sqlTagMapper;
    }
}
