package io.github.rocketk.jorm.mapper.column;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author pengyu
 * @date 2022/8/23
 */
class DelimiterBasedStringArrayColumnFieldMapperTest {

    @Test
    void fieldToColumn() {
        final DelimiterBasedStringArrayColumnFieldMapper mapper = new DelimiterBasedStringArrayColumnFieldMapper(" ");
        boolean[] booleans = {true, false};
        assertEquals("true false", mapper.fieldToColumn(booleans));
        BigDecimal[] decimals = {new BigDecimal(100), new BigDecimal(200)};
        assertEquals("100 200", mapper.fieldToColumn(decimals));
    }
}