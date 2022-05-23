package io.github.rocketk.jorm.mapper.column;

import java.util.Arrays;
import java.util.List;

/**
 * @author pengyu
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
        final String[] arrOfStr = columnValue.split(delimiter);
        if (String[].class.isAssignableFrom(fieldType)) {
            return (T) arrOfStr;
        }
        if (Integer[].class.isAssignableFrom(fieldType)) {
            final Integer[] arr = Arrays.stream(arrOfStr).map(Integer::parseInt).toArray(Integer[]::new);
            return (T) arr;
        }
        if (int[].class.isAssignableFrom(fieldType)) {
            final int[] arr = Arrays.stream(arrOfStr).mapToInt(Integer::parseInt).toArray();
            return (T) arr;
        }
        if (Long[].class.isAssignableFrom(fieldType)) {
            final Long[] arr = Arrays.stream(arrOfStr).map(Long::parseLong).toArray(Long[]::new);
            return (T) arr;
        }
        if (long[].class.isAssignableFrom(fieldType)) {
            final long[] arr = Arrays.stream(arrOfStr).mapToLong(Long::parseLong).toArray();
            return (T) arr;
        }
        if (List.class.isAssignableFrom(fieldType)) {
            return (T) Arrays.asList(arrOfStr);
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
        if (array instanceof int[]) {
            int[] arrayObject = (int[]) array;
            return String.join(this.delimiter, Arrays.stream(arrayObject).mapToObj(i -> Integer.valueOf(i).toString()).toArray(String[]::new));
        }
        if (array instanceof Long[]) {
            Long[] arrayObject = (Long[]) array;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        if (array instanceof long[]) {
            long[] arrayObject = (long[]) array;
            return String.join(this.delimiter, Arrays.stream(arrayObject).mapToObj(i -> Long.valueOf(i).toString()).toArray(String[]::new));
        }
        if (array instanceof Boolean[]) {
            Boolean[] arrayObject = (Boolean[]) array;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        if (array instanceof List) {
            List list = (List) array;
            return String.join(this.delimiter, list);
        }
        // todo 支持更多的数组类型
        throw new CannotParseColumnToFieldException("unsupported fieldType: " + array.getClass().getCanonicalName());
//        if (array instanceof int[]) {
//            int[] arrayObject = (int[]) array;
//            return String.join(this.delimiter, Arrays.stream(arrayObject).map((int i)->Integer.valueOf(i)).toArray(String[]::new));
//        }
    }


}
