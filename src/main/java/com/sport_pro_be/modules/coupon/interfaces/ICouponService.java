package com.sport_pro_be.modules.coupon.interfaces;

import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.coupon.domain.Coupon;
import java.math.BigDecimal;

public interface ICouponService {
    Coupon validateAndGetCoupon(String code, User user, BigDecimal orderAmount);
    BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount);
    BigDecimal calculateSalePrice(Coupon coupon, BigDecimal originalPrice);
}
