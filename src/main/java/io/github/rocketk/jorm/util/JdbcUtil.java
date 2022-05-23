package io.github.rocketk.jorm.util;

import io.github.rocketk.jorm.err.JormQueryException;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author pengyu
 */
public class JdbcUtil {

    private static Class[] supportedType = new Class[]{
            String.class,
            Integer.class,
            int.class,
            Long.class,
            long.class,
            Byte.class,
            byte.class,
            Short.class,
            short.class,
            Boolean.class,
            boolean.class,
            BigDecimal.class,
            Date.class,
            byte[].class,
    };

    /**
     * To determine whether the field type is supported for directly parsing from a JDBC type
     *
     * @param fieldType the type of the field
     * @return true if the type is supported
     */
    public static boolean isSupportedTypeByJdbc(Class fieldType) {
        for (Class c : supportedType) {
            if (c.isAssignableFrom(fieldType)) {
                return true;
            }
        }
        return false;
    }

    public static Object getFromResultSet(ResultSet rs, int index, Class fieldType) throws SQLException {
        if (String.class.isAssignableFrom(fieldType)) {
            final String value = rs.getString(index);
            return rs.wasNull() ? null : value;
        } else if (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)) {
            final int value = rs.getInt(index);
            return rs.wasNull() ? null : value;
        } else if (Long.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)) {
            final long value = rs.getLong(index);
            return rs.wasNull() ? null : value;
        } else if (Byte.class.isAssignableFrom(fieldType) || byte.class.isAssignableFrom(fieldType)) {
            final byte value = rs.getByte(index);
            return rs.wasNull() ? null : value;
        } else if (Short.class.isAssignableFrom(fieldType) || short.class.isAssignableFrom(fieldType)) {
            final short value = rs.getShort(index);
            return rs.wasNull() ? null : value;
        } else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
            final boolean value = rs.getBoolean(index);
            return rs.wasNull() ? null : value;
        } else if (BigDecimal.class.isAssignableFrom(fieldType)) {
            return rs.getBigDecimal(index);
        } else if (Date.class.isAssignableFrom(fieldType)) {
//            final Timestamp timestamp = rs.getTimestamp(index);
//            return timestamp == null ? null : new Date(timestamp.getTime());
//            return rs.getDate(index);
            final String columnClassName = rs.getMetaData().getColumnClassName(index);
            if (java.sql.Date.class.getCanonicalName().equals(columnClassName)) {
                return rs.getDate(index);
            }
            if (java.sql.Time.class.getCanonicalName().equals(columnClassName)) {
                return rs.getTime(index);
            }
            if (java.sql.Timestamp.class.getCanonicalName().equals(columnClassName)) {
                return rs.getTimestamp(index);
            }
            if (java.time.LocalDateTime.class.getCanonicalName().equals(columnClassName)) {
                return rs.getTimestamp(index);
            }
            return rs.getTimestamp(index);
//            throw new JormQueryException("unsupported columnClassName: " + columnClassName);
        } else if (byte[].class.isAssignableFrom(fieldType)) {
            return rs.getBytes(index);
        }
//        throw new JormQueryException("unsupported fieldType " + fieldType.getCanonicalName());
        return getFromResultSetAccordingJdbcType(rs, index);
    }

    private static Object getFromResultSetAccordingJdbcType(ResultSet rs, int index) throws SQLException {
        final String columnClassName = rs.getMetaData().getColumnClassName(index);
        if (String.class.getCanonicalName().equals(columnClassName)) {
            final String value = rs.getString(index);
            return rs.wasNull() ? null : value;
        } else if (Integer.class.getCanonicalName().equals(columnClassName) || int.class.getCanonicalName().equals(columnClassName)) {
            final int value = rs.getInt(index);
            return rs.wasNull() ? null : value;
        } else if (Long.class.getCanonicalName().equals(columnClassName) || long.class.getCanonicalName().equals(columnClassName)) {
            final long value = rs.getLong(index);
            return rs.wasNull() ? null : value;
        } else if (Byte.class.getCanonicalName().equals(columnClassName) || byte.class.getCanonicalName().equals(columnClassName)) {
            final byte value = rs.getByte(index);
            return rs.wasNull() ? null : value;
        } else if (Short.class.getCanonicalName().equals(columnClassName) || short.class.getCanonicalName().equals(columnClassName)) {
            final short value = rs.getShort(index);
            return rs.wasNull() ? null : value;
        } else if (Boolean.class.getCanonicalName().equals(columnClassName) || boolean.class.getCanonicalName().equals(columnClassName)) {
            // 由于 mysql 中 tinyint(1) 类型的列，在 jdbc driver 中会当成 Boolean 类型，这样会导致精度被降低
            // 因此我们要还原数据库中的数字
            final int value = rs.getByte(index);
            return rs.wasNull() ? null : value;
        } else if (BigDecimal.class.getCanonicalName().equals(columnClassName)) {
            return rs.getBigDecimal(index);
        } else if (java.sql.Date.class.getCanonicalName().equals(columnClassName)) {
            return rs.getDate(index);
        } else if (java.sql.Timestamp.class.getCanonicalName().equals(columnClassName)) {
            return rs.getTimestamp(index);
        } else if (java.sql.Time.class.getCanonicalName().equals(columnClassName)) {
            return rs.getTime(index);
        } else if (byte[].class.getCanonicalName().equals(columnClassName)) {
            return rs.getBytes(index);
        }
        throw new JormQueryException("unsupported columnClassName " + columnClassName);
    }

    /**
     * To set the argument into the PreparedStatement object and return true if no exception thrown.
     *
     * @param ps             PreparedStatement object
     * @param parameterIndex the index of the argument, starts from 1
     * @param arg            the argument
     * @return true if no exception thrown; false if the type of the argument is not supported
     * @throws SQLException if any exception thrown from method PreparedStatement.setXXX()
     */
    public static boolean setArgWithoutConversion(PreparedStatement ps, int parameterIndex, Object arg) throws SQLException {
        if (arg == null) {
//            ps.setNull(parameterIndex, ps.getParameterMetaData().getParameterType(parameterIndex));
            ps.setObject(parameterIndex, null);
            return true;
        }
        if (arg instanceof String) {
            ps.setString(parameterIndex, (String) arg);
            return true;
        }
        if (arg instanceof Integer) {
            ps.setInt(parameterIndex, (Integer) arg);
            return true;
        }
        if (arg instanceof Long) {
            ps.setLong(parameterIndex, (Long) arg);
            return true;
        }
        if (arg instanceof BigDecimal) {
            ps.setBigDecimal(parameterIndex, (BigDecimal) arg);
            return true;
        }
        if (arg instanceof Boolean) {
            ps.setBoolean(parameterIndex, (Boolean) arg);
            return true;
        }
        if (arg instanceof Date) {
            ps.setTimestamp(parameterIndex, new java.sql.Timestamp(((Date) arg).getTime()));
            return true;
        }
        if (arg instanceof Float) {
            ps.setFloat(parameterIndex, (Float) arg);
            return true;
        }
        if (arg instanceof Double) {
            ps.setDouble(parameterIndex, (Double) arg);
            return true;
        }
        if (arg instanceof Short) {
            ps.setShort(parameterIndex, (Short) arg);
            return true;
        }
        if (arg instanceof byte[]) {
            ps.setBlob(parameterIndex, new ByteArrayInputStream((byte[]) arg));
            return true;
        }
//        throw new JormQueryException("unsupported argument type " + arg.getClass().getCanonicalName());
        return false;
    }

    public static void executeUpdateBatch(PreparedStatement ps) {

    }
}
