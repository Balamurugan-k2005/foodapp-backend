package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.ReviewDto;
import com.ecommerce.app.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "productId", source = "product.id")
    ReviewDto toDto(Review review);
}
