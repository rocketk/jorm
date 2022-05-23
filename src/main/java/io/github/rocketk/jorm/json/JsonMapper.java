package io.github.rocketk.jorm.json;

/**
 * Json的序列化和反序列化工具
 * @author pengyu
 * @date 2021/12/22
 */
public interface JsonMapper {
    /**
     * 将一个字符串还原为一个对象
     *
     * @param content json字符串
     * @param clazz   类型
     * @param <T>     需要还原的对象类型
     * @return
     */
    <T> T unmarshal(String content, Class<T> clazz);

    /**
     * 将一个对象序列化为一个字符串
     *
     * @param obj
     * @return
     */
    String marshal(Object obj);
}
