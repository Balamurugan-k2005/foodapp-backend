package com.ecommerce.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String cuisineType;
    private Integer deliveryTime;
    private BigDecimal averagePrice;
    private boolean active;
    private Long ownerId;
    private String ownerName;
}
