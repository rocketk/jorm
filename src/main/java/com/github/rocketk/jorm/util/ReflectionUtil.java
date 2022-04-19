package com.github.rocketk.jorm.util;

import com.github.rocketk.jorm.anno.JormColumn;
import com.github.rocketk.jorm.anno.JormIgnore;
import com.github.rocketk.jorm.anno.JormJsonObject;
import com.github.rocketk.jorm.anno.JormTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 * @author pengyu
 * @date 2022/3/28
 */
public class ReflectionUtil {
    private final static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);
//    private final Cache<Class, String> tableNameCache = CacheBuilder.newBuilder()
//            .maximumSize(1000)
//            .expireAfterWrite(60,TimeUnit.MINUTES)
//            .build();

//    public static <T> boolean hasField(Class<T> model, String fieldName) {
//        try {
//            final Field f = model.getDeclaredField(fieldName);
//            return true;
//        } catch (NoSuchFieldException e) {
//            return false;
//        }
//    }

    public static <T> String tableName(Class<T> model) {
        final JormTable t = model.getAnnotation(JormTable.class);
        if (t != null) {
            return t.name();
        }
        return null;
    }

    public static <T> Optional<String> deletedAtColumn(Class<T> model) {
        final JormTable t = model.getAnnotation(JormTable.class);
        if (t != null && t.enableSoftDelete() && StringUtils.isNotBlank(t.deletedAtColumn())) {
            return Optional.of(t.deletedAtColumn());
        }
        return Optional.empty();
    }

    public static <T> Optional<String> createdAtColumn(Class<T> model) {
        final JormTable t = model.getAnnotation(JormTable.class);
        return Optional.ofNullable(t.autoGenerateCreatedAt() ? t.createdAtColumn() : null);
    }

    public static <T> Optional<String> updatedAtColumn(Class<T> model) {
        final JormTable t = model.getAnnotation(JormTable.class);
        return Optional.ofNullable(t.autoGenerateUpdatedAt() ? t.updatedAtColumn() : null);
    }

//    public static <T> void setupCreatedAt(T object) {
//        final Class<?> model = object.getClass();
//        final JormTable t = model.getAnnotation(JormTable.class);
//        if (t == null || !t.autoGenerateCreatedAt()) {
//            return;
//        }
//        try {
//            final Field field = model.getDeclaredField(t.createdAtField());
//            setupFieldValue(object, field);
//        } catch (NoSuchFieldException e) {
//            logger.error("no such field for {}", t.createdAtField());
//        } catch (IllegalAccessException e) {
//            logger.error("failed to set new Date() to createdAt field, caused by: {}", e.getMessage());
//        }
//    }
//
//    public static <T> void setupUpdatedAt(T object) {
//        final Class<?> model = object.getClass();
//        final JormTable t = model.getAnnotation(JormTable.class);
//        if (t == null || !t.autoGenerateUpdatedAt()) {
//            return;
//        }
//        try {
//            final Field field = model.getDeclaredField(t.updatedAtField());
//            setupFieldValue(object, field);
//        } catch (NoSuchFieldException e) {
//            logger.error("no such field for {}", t.updatedAtField());
//        } catch (IllegalAccessException e) {
//            logger.error("failed to set new Date() to updatedAt field, caused by: {}", e.getMessage());
//        }
//    }

//    private static <T> void setupFieldValue(T object, Field field) throws IllegalAccessException {
//        field.setAccessible(true);
//        final Class<?> type = field.getType();
//        if (!Date.class.isAssignableFrom(type)) {
//            throw new IllegalArgumentException("type of Date is not assignable from the type of field " + type.getCanonicalName());
//        }
//        if (field.get(object) == null) {
//            field.set(object, new Date());
//        }
//    }


//    public static <T> boolean onlyFindNonDeletedByAnnotation(Class<T> model) {
//        final JormTable t = model.getAnnotation(JormTable.class);
//        return t != null && t.onlyFindNonDeleted();
//    }

    public static Optional<String> columnName(Field field) {
        final JormColumn column = field.getAnnotation(JormColumn.class);
        if (column != null && StringUtils.isNotBlank(column.name())) {
            return Optional.of(column.name());
        }
        return Optional.empty();
//        return null;
    }

    public static boolean shouldUseJson(Class<?> type) {
//        final JormColumn column = field.getAnnotation(JormColumn.class);
//        if (column != null) {
//            return column.jsonField();
//        }
        if (Map.class.isAssignableFrom(type)) {
            return true;
        }
        final JormJsonObject jsonAnnotation = type.getAnnotation(JormJsonObject.class);
        return jsonAnnotation != null;
    }

    public static boolean shouldIgnoreReadFromDb(Field field) {
        final JormIgnore ignore = field.getAnnotation(JormIgnore.class);
        return ignore != null && ignore.ignoreReadFromDb();
    }

    public static boolean shouldIgnoreWriteToDb(Field field) {
        final JormIgnore ignore = field.getAnnotation(JormIgnore.class);
        return ignore != null && ignore.ignoreWriteToDb();
    }

    public static <T extends Enum<T>> Object getValueForCustomEnum(T enumObject, String valueMethod) {
        try {
            final Method m = enumObject.getClass().getMethod(valueMethod);
//            m.setAccessible(true);
//            final Object value = m.invoke(enumObject);
//            final Class<?> returnType = m.getReturnType();
//            return new ObjectAndType(returnType, value);
            return m.invoke(enumObject);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("no such method '%s' to invoke for class '%s', caused by: %s",
                    valueMethod, enumObject.getClass().getCanonicalName(), e.getMessage()), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("failed to invoke method '%s' for class '%s', caused by: %s",
                    valueMethod, enumObject.getClass().getCanonicalName(), e.getMessage()), e);
        }
    }

    public static <T extends Enum<T>> T parseForCustomEnum(Class<T> enumType, String parseMethod, Object rawValue) {
        try {
            final Method m = enumType.getMethod(parseMethod, Object.class);
            final Object enumObj = m.invoke(null, rawValue);
            if (enumObj == null) {
                throw new NullPointerException(String.format("cannot parse the rawValue '%s' into enum object of type '%s'. the parsed object is null", rawValue, enumType.getCanonicalName()));
            }
            if (!enumType.isAssignableFrom(enumObj.getClass())) {
                throw new IllegalArgumentException(String.format("cannot parse the rawValue '%s' into enum object. the parsed object is not an instance of type '%s'", rawValue, enumType.getCanonicalName()));
            }
            return (T) enumObj;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("no such method '%s' to invoke for class '%s', caused by: %s",
                    parseMethod, enumType.getCanonicalName(), e.getMessage()), e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("failed to invoke method '%s' for class '%s', caused by: %s",
                    parseMethod, enumType.getCanonicalName(), e.getMessage()), e);
        }

    }
}
