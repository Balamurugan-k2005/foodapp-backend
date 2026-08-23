package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.CouponDto;
import com.ecommerce.app.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponDto toDto(Coupon coupon);

    @Mapping(target = "isActive", source = "active")
    Coupon toEntity(CouponDto couponDto);
}
