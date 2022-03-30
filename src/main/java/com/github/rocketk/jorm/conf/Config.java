package com.github.rocketk.jorm.conf;

/**
 * @author pengyu
 * @date 2022/3/24
 */
public class Config {
    public static final String JACKSON = "jackson";
    public static final String GSON = "gson";
    public static final String FASTJSON = "fastjson";
    /**
     * 数组分隔符，用来将数据库列中的字符串值分隔成Java中的字符串数组对象
     */
    private String arrayDelimiter;
    /**
     * 使用何种JSON序列化器
     * jackson, gson, fastjson
     */
    private String jsonProvider;
    private boolean printSql;

    public String getArrayDelimiter() {
        return arrayDelimiter;
    }

    public void setArrayDelimiter(String arrayDelimiter) {
        this.arrayDelimiter = arrayDelimiter;
    }

    public String getJsonProvider() {
        return jsonProvider;
    }

    public void setJsonProvider(String jsonProvider) {
        this.jsonProvider = jsonProvider;
    }

    public boolean isPrintSql() {
        return printSql;
    }

    public void setPrintSql(boolean printSql) {
        this.printSql = printSql;
    }
}
