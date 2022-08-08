package io.github.rocketk.jorm.util;

import java.util.List;

/**
 * @author pengyu
 * @date 2022/8/8
 */
public class ArrayUtil {
    public static Object[] toArray(List list) {
        if (list == null) {
            return null;
        }
        return list.toArray();
    }
}
