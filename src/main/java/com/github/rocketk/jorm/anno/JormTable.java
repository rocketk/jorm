package com.github.rocketk.jorm.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pengyu
 * @date 2022/3/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JormTable {
    String name();

    String primaryKeyName() default "pk";

    boolean onlyFindNonDeleted() default true;

    boolean softDelete() default true;

    String deletedAtField() default "deletedAt";

    String deletedAtColumn() default "deleted_at";

    boolean autoGenerateCreatedAt() default true;

    String createdAtField() default "createdAt";

    String createdAtColumn() default "created_at";

    boolean autoGenerateUpdatedAt() default true;

    String updatedAtField() default "updatedAt";

    String updatedAtColumn() default "updated_at";

}
