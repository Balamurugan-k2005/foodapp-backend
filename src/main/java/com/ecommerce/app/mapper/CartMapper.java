package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.CartDto;
import com.ecommerce.app.dto.CartItemDto;
import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalAmount", source = "items", qualifiedByName = "calculateCartTotal")
    CartDto toDto(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    CartItemDto toItemDto(CartItem cartItem);

    @Named("calculateCartTotal")
    default BigDecimal calculateCartTotal(List<CartItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
