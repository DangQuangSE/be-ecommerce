package com.sport_pro_be.modules.custom_design.service;

import java.math.BigDecimal;

final class PrintingPriceCalculator {

    private PrintingPriceCalculator() {
    }

    static BigDecimal calculate(
            int numTextLines,
            int numImages,
            BigDecimal textUnitPrice,
            BigDecimal imageUnitPrice) {
        return textUnitPrice.multiply(BigDecimal.valueOf(numTextLines))
                .add(imageUnitPrice.multiply(BigDecimal.valueOf(numImages)));
    }
}
