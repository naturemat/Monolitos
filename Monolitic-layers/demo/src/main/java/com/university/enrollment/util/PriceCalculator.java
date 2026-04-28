package com.university.enrollment.util;

import java.math.BigDecimal;

public final class PriceCalculator {

    private static final BigDecimal STANDARD_PRICE = new BigDecimal("100.00");
    private static final BigDecimal DISCOUNT_PRICE = new BigDecimal("85.00");

    private PriceCalculator() {
        // utility class
    }

    public static BigDecimal calculatePrice(int existingEnrollmentCount) {
        return existingEnrollmentCount >= 3 ? DISCOUNT_PRICE : STANDARD_PRICE;
    }
}
