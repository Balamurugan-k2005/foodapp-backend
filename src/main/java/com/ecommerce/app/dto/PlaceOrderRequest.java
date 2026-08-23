package com.ecommerce.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotNull(message = "Delivery address selection is required")
    private Long addressId;
    private String couponCode;
}
