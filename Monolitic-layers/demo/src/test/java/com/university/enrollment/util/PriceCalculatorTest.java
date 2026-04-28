package com.university.enrollment.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    @Test
    void calculatePrice_lessThan3Enrollments_returnsStandard() {
        assertEquals(new BigDecimal("100.00"), PriceCalculator.calculatePrice(0));
        assertEquals(new BigDecimal("100.00"), PriceCalculator.calculatePrice(2));
    }

    @Test
    void calculatePrice_3OrMoreEnrollments_returnsDiscount() {
        assertEquals(new BigDecimal("85.00"), PriceCalculator.calculatePrice(3));
        assertEquals(new BigDecimal("85.00"), PriceCalculator.calculatePrice(5));
        assertEquals(new BigDecimal("85.00"), PriceCalculator.calculatePrice(10));
    }
}
