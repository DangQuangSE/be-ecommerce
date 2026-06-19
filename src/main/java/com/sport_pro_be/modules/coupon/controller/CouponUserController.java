package com.sport_pro_be.modules.coupon.controller;

import com.sport_pro_be.common.ApiResponse;
import com.sport_pro_be.common.SecurityUtils;
import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.repository.UserRepository;
import com.sport_pro_be.modules.coupon.constant.CouponMessageConstant;
import com.sport_pro_be.modules.coupon.dto.CouponResponse;
import com.sport_pro_be.modules.coupon.interfaces.ICouponService;
import com.sport_pro_be.modules.order.constant.OrderMessageConstant;
import com.sport_pro_be.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CouponUserController {

    private final ICouponService couponService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getMyAvailableCoupons() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.USER_NOT_FOUND));
        List<CouponResponse> response = couponService.getActiveCoupons(user);
        return ResponseEntity.ok(ApiResponse.of(CouponMessageConstant.COUPONS_RETRIEVED, response));
    }
}
