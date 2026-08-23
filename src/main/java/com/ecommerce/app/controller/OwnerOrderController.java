package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ApiResponse;
import com.ecommerce.app.dto.OrderDto;
import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.OrderStatus;
import com.ecommerce.app.entity.Restaurant;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.OrderMapper;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.RestaurantRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class OwnerOrderController {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OwnerOrderController(OrderRepository orderRepository,
                                RestaurantRepository restaurantRepository,
                                UserRepository userRepository,
                                OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOwnerOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found"));

        Restaurant restaurant = restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant linked to this owner account"));

        List<OrderDto> list = orderRepository.findByRestaurantOrderByCreatedAtDesc(restaurant).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Restaurant orders fetched successfully", list));
    }

    @PutMapping("/orders/{id}/status")
    @Transactional
    public ResponseEntity<ApiResponse<OrderDto>> updateOwnerOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found"));

        Restaurant restaurant = restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant linked to this owner account"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!order.getRestaurant().getId().equals(restaurant.getId())) {
            throw new RuntimeException("Unauthorized update. This order belongs to another restaurant.");
        }

        order.setStatus(status);
        Order updated = orderRepository.save(order);
        
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", orderMapper.toDto(updated)));
    }
}
