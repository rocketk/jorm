package io.github.rocketk.jorm.mapper.column;

/**
 * @author pengyu
 */
public interface StringArrayColumnFieldMapper {
    /**
     * read string value from database and convert it into Java object in type of T
     *
     * @param columnValue string value read from database
     * @param fieldType the type of the field
     * @param <T> the type of the field. Array or List
     * @return the field object in type of T
     */
    <T> T columnToField(String columnValue, Class<T> fieldType);

    /**
     * convert the value of the field into a string for storing in database
     *
     * @param fieldValue the value of the field. Array or List
     * @return the string value that will be written into database
     */
    String fieldToColumn(Object fieldValue);
}
