package io.github.rocketk.jorm.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标识一个枚举类，在Java对象与数据库列值之间转换时要使用自定义的方法。
 * 当一个枚举类被标记此注解后，JORM将会依据parseMethod和valueMethod来实现动态映射
 *
 * @author pengyu
 * @date 2022/3/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JormCustomEnum {
    String parseMethod() default "parse";

    String valueMethod() default "getValue";
}
