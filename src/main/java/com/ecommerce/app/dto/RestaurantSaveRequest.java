package com.ecommerce.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RestaurantSaveRequest {
    @NotBlank(message = "Restaurant name is required")
    private String name;

    private String description;
    private String imageUrl;
    private String cuisineType;
    private Integer deliveryTime;
    private BigDecimal averagePrice;
    private Long ownerId;
}
