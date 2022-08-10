package io.github.rocketk.jorm.util;

import java.util.List;

/**
 * @author pengyu
 */
public class ArrayUtil {
    public static Object[] toArray(List list) {
        if (list == null) {
            return null;
        }
        return list.toArray();
    }
}
