package io.github.rocketk.jorm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author pengyu
 */
public class DateUtil {

    public static Date toDate(String text) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(text);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date toDateTime(String text) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
