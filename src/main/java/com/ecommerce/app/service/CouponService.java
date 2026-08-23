package com.ecommerce.app.service;

import com.ecommerce.app.dto.CouponDto;
import com.ecommerce.app.entity.Coupon;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.CouponMapper;
import com.ecommerce.app.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    public CouponService(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    @Transactional
    public CouponDto createCoupon(CouponDto couponDto) {
        if (couponRepository.findByCode(couponDto.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }

        Coupon coupon = couponMapper.toEntity(couponDto);
        coupon.setActive(true);
        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toDto(savedCoupon);
    }

    public List<CouponDto> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(couponMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponDto toggleCouponStatus(Long id, boolean active) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        coupon.setActive(active);
        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toDto(savedCoupon);
    }

    public CouponDto getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return couponMapper.toDto(coupon);
    }
}
