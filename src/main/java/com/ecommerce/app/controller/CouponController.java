package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ApiResponse;
import com.ecommerce.app.dto.CouponDto;
import com.ecommerce.app.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> createCoupon(@Valid @RequestBody CouponDto couponDto) {
        CouponDto created = couponService.createCoupon(couponDto);
        return new ResponseEntity<>(ApiResponse.success("Coupon created successfully", created), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getAllCoupons() {
        List<CouponDto> list = couponService.getAllCoupons();
        return ResponseEntity.ok(ApiResponse.success("Coupons fetched successfully", list));
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> toggleCoupon(@PathVariable Long id, @RequestParam boolean active) {
        CouponDto updated = couponService.toggleCouponStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("Coupon status toggled successfully", updated));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponDto>> getCouponByCode(@PathVariable String code) {
        CouponDto coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Coupon retrieved successfully", coupon));
    }
}
