package com.ecommerce.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private BigDecimal totalAmount;
    private String status;
    private Long userId;
    private Long restaurantId;
    private String restaurantName;
    private AddressDto address;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
}
