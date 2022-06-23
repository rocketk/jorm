package io.github.rocketk.jorm.json;

/**
 * Json tool for marshalling and unmarshalling
 *
 * @author pengyu
 */
public interface JsonMapper {
    /**
     * To unmarshal a string to a Java object
     *
     * @param content the string in JSON format
     * @param clazz   type class
     * @param <T>     the expected type of the target object
     * @return unmarshalled object
     */
    <T> T unmarshal(String content, Class<T> clazz);

    /**
     * To marshal a Java object to a string in JSON format
     *
     * @param obj the object to be marshaled
     * @return the string in JSON format
     */
    String marshal(Object obj);
}
