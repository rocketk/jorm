package io.github.rocketk.jorm.mapper.column;

/**
 * @author pengyu
 * @date 2022/3/24
 */
public interface StringArrayColumnFieldMapper {
    /**
     * 将列中的字符串值转换成字符串数组
     *
     * @param columnValue 从列中读取的字符串值
     * @return
     */
    <T> T columnToField(String columnValue, Class<T> fieldType);

    /**
     * 将Java字段的值转换成字符串
     *
     * @param array Java字段值--数组
     * @return
     */
    String fieldToColumn(Object array);
}
