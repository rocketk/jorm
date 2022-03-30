package com.github.rocketk.jorm.mapper.column;

import java.util.Arrays;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public class DelimiterBasedStringArrayColumnFieldMapper implements StringArrayColumnFieldMapper {
    private final String delimiter;

    public DelimiterBasedStringArrayColumnFieldMapper(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T columnToField(String columnValue, Class<T> fieldType) {
        if (columnValue == null) {
            return null;
        }
        final String[] split = columnValue.split(delimiter);
        if (String[].class.isAssignableFrom(fieldType)) {
            return (T) split;
        }
        if (Integer[].class.isAssignableFrom(fieldType)) {
            final Integer[] arr = Arrays.stream(split).map(Integer::parseInt).toArray(Integer[]::new);
            return (T) arr;
        }
        if (int[].class.isAssignableFrom(fieldType)) {
            final int[] arr = Arrays.stream(split).mapToInt(Integer::parseInt).toArray();
            return (T) arr;
        }
        if (Long[].class.isAssignableFrom(fieldType)) {
            final Long[] arr = Arrays.stream(split).map(Long::parseLong).toArray(Long[]::new);
            return (T) arr;
        }
        if (long[].class.isAssignableFrom(fieldType)) {
            final long[] arr = Arrays.stream(split).mapToLong(Long::parseLong).toArray();
            return (T) arr;
        }
        throw new CannotParseColumnToFieldException("unsupported fieldType: " + fieldType.getCanonicalName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public String fieldToColumn(Object array) {
        if (array == null) {
            return null;
        }
        if (array instanceof String[]) {
            String[] arrayObject = (String[]) array;
            return String.join(this.delimiter, arrayObject);
        }
        if (array instanceof Integer[]) {
            Integer[] arrayObject = (Integer[]) array;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        // todo 支持更多的数组类型
        throw new CannotParseColumnToFieldException("unsupported fieldType: " + array.getClass().getCanonicalName());
//        if (array instanceof int[]) {
//            int[] arrayObject = (int[]) array;
//            return String.join(this.delimiter, Arrays.stream(arrayObject).map((int i)->Integer.valueOf(i)).toArray(String[]::new));
//        }
    }


}
