package io.github.rocketk.jorm.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pengyu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JormTable {
    String name();

    String primaryKeyName() default "pk";

    boolean enableSoftDelete() default false;

    String deletedAtColumn() default "deleted_at";

    boolean autoGenerateCreatedAt() default false;

    String createdAtColumn() default "created_at";

    boolean autoGenerateUpdatedAt() default false;

    String updatedAtColumn() default "updated_at";

}
