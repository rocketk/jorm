package io.github.rocketk.jorm.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pengyu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JormIgnore {
    boolean ignoreReadFromDb() default true;

    boolean ignoreWriteToDb() default true;

}
