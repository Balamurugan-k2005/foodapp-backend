package com.ecommerce.app.mapper;

import com.ecommerce.app.dto.ProductDto;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "restaurantName", source = "restaurant.name")
    @Mapping(target = "averageRating", source = "reviews", qualifiedByName = "calculateAverageRating")
    @Mapping(target = "active", source = "active")
    ProductDto toDto(Product product);

    @Named("calculateAverageRating")
    default double calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
