package com.github.rocketk;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void test() {
        System.out.println(Short.class.isAssignableFrom(Long.class));
        System.out.println(Integer.class.isAssignableFrom(Long.class));
        System.out.println(Long.class.isAssignableFrom(Integer.class));
        System.out.println(byte[].class.toString());
        System.out.println(Byte[].class.isAssignableFrom(byte[].class));
        int a = 1;
        Object b = a;
        System.out.println(b.getClass().getCanonicalName());
        System.out.println(int.class.getCanonicalName());
    }
}
