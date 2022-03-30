package com.github.rocketk.jorm;

import com.github.rocketk.jorm.anno.JormColumn;
import com.github.rocketk.jorm.anno.JormIgnore;
import com.github.rocketk.jorm.anno.JormJsonObject;
import com.github.rocketk.jorm.anno.JormTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author pengyu
 * @date 2022/3/28
 */
public class ReflectionUtil {
    public static <T> boolean hasField(Class<T> model, String fieldName) {
        try {
            final Field f = model.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static <T> String tableName(Class<T> model) {
        final JormTable t = model.getAnnotation(JormTable.class);
        if (t != null) {
            return t.name();
        }
        return null;
    }

    public static <T> String deletedAtColumn(Class<T> model) {
        final JormTable t = model.getAnnotation(JormTable.class);
        if (t != null) {
            return t.deletedAtColumn();
        }
        return "deleted_at";
    }

    public static <T> boolean onlyFindNonDeletedByAnnotation(Class<T> model) {
        final JormTable t = model.getAnnotation(JormTable.class);
        if (t != null) {
            return t.onlyFindNonDeleted();
        }
        return hasField(model, "deletedAt");
    }

    public static String columnName(Field field) {
        final JormColumn column = field.getAnnotation(JormColumn.class);
        if (column != null) {
            return column.name();
        }
        return null;
    }

    public static boolean shouldUseJson(Class<?> object) {
//        final JormColumn column = field.getAnnotation(JormColumn.class);
//        if (column != null) {
//            return column.jsonField();
//        }
        final JormJsonObject jsonAnnotation = object.getAnnotation(JormJsonObject.class);
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
