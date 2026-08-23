package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.OrderDto;
import com.ecommerce.app.dto.OrderItemDto;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "restaurantName", source = "restaurant.name")
    OrderDto toDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    OrderItemDto toItemDto(OrderItem orderItem);
}
