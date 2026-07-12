package com.sport_pro_be.modules.custom_design.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PrintingPriceCalculatorTest {

    @Test
    void calculate_usesOnlyElementUnitPrices_withoutMaterialBaseFee() {
        BigDecimal total = PrintingPriceCalculator.calculate(
                1,
                0,
                BigDecimal.valueOf(20000),
                BigDecimal.valueOf(30000));

        assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(20000));
    }
}
