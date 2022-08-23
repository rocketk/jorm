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
    public String fieldToColumn(Object fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue instanceof String[]) {
            String[] arrayObject = (String[]) fieldValue;
            return String.join(this.delimiter, arrayObject);
        }
        if (fieldValue instanceof Integer[]) {
            Integer[] arrayObject = (Integer[]) fieldValue;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        if (fieldValue instanceof int[]) {
            int[] arrayObject = (int[]) fieldValue;
            return String.join(this.delimiter, Arrays.stream(arrayObject).mapToObj(i -> Integer.valueOf(i).toString()).toArray(String[]::new));
        }
        if (fieldValue instanceof Long[]) {
            Long[] arrayObject = (Long[]) fieldValue;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        if (fieldValue instanceof long[]) {
            long[] arrayObject = (long[]) fieldValue;
            return String.join(this.delimiter, Arrays.stream(arrayObject).mapToObj(i -> Long.valueOf(i).toString()).toArray(String[]::new));
        }
        if (fieldValue instanceof Boolean[]) {
            Boolean[] arrayObject = (Boolean[]) fieldValue;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        if (fieldValue instanceof boolean[]) {
            boolean[] booleans = (boolean[]) fieldValue;
            String[] arrayObject = new String[booleans.length];
            for (int i = 0; i < booleans.length; i++) {
                arrayObject[i] = booleans[i] + "";
            }
            return String.join(this.delimiter, arrayObject);
        }
        if (fieldValue instanceof Short[]) {
            Short[] arrayObject = (Short[]) fieldValue;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        if (fieldValue instanceof short[]) {
            short[] shorts = (short[]) fieldValue;
            String[] arrayObject = new String[shorts.length];
            for (int i = 0; i < shorts.length; i++) {
                arrayObject[i] = shorts[i] + "";
            }
            return String.join(this.delimiter, arrayObject);
        }
        if (fieldValue instanceof Object[]) {
            Object[] arrayObject = (Object[]) fieldValue;
            return String.join(this.delimiter, Arrays.stream(arrayObject).map(Object::toString).toArray(String[]::new));
        }
        if (fieldValue instanceof List) {
            List list = (List) fieldValue;
            return String.join(this.delimiter, list);
        }
        // todo 支持更多的数组类型
        throw new CannotParseColumnToFieldException(String.format("unsupported fieldType: %s", fieldValue.getClass().getCanonicalName()));
//        if (array instanceof int[]) {
//            int[] arrayObject = (int[]) array;
//            return String.join(this.delimiter, Arrays.stream(arrayObject).map((int i)->Integer.valueOf(i)).toArray(String[]::new));
//        }
    }


}
