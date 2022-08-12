package io.github.rocketk.jorm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pengyu
 * @date 2022/8/11
 */
public class StringUtilsTest {

    @Test
    void escapeWhiteCharsUseRegex() {
        assertEquals("a b c", StringUtils.escapeWhiteCharsUseRegex("a b  c"));
        assertEquals("a b c", StringUtils.escapeWhiteCharsUseRegex("a b \n c"));
        assertEquals("a b c", StringUtils.escapeWhiteCharsUseRegex("a b \r c"));
        assertEquals("a b c", StringUtils.escapeWhiteCharsUseRegex("a b c\n"));
        assertEquals("a b c", StringUtils.escapeWhiteCharsUseRegex("a b c\r\n"));
        assertEquals("a b c", StringUtils.escapeWhiteCharsUseRegex("\na b c\r\n"));
        assertEquals("a b c", StringUtils.escapeWhiteCharsUseRegex(" a b c\r\n"));
    }
}