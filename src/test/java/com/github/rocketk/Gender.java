package com.github.rocketk;

import com.github.rocketk.jorm.anno.JormCustomEnum;

/**
 * @author pengyu
 * @date 2022/3/28
 */
@JormCustomEnum
public enum Gender {
    FEMALE(0),
    MALE(1);
    private int value;

    Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Gender parse(Object rawValue) {
        Integer value = (Integer) rawValue;
        for (Gender d : Gender.values()) {
            if (d.value == value) {
                return d;
            }
        }
        throw new IllegalArgumentException(String.format("no such value '%d' for Gender", value));
    }
}
