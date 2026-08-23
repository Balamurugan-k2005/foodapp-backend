package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.WishlistDto;
import com.ecommerce.app.entity.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface WishlistMapper {

    @Mapping(target = "userId", source = "user.id")
    WishlistDto toDto(Wishlist wishlist);
}
