package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ApiResponse;
import com.ecommerce.app.dto.OrderDto;
import com.ecommerce.app.dto.PlaceOrderRequest;
import com.ecommerce.app.entity.OrderStatus;
import com.ecommerce.app.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Customer Endpoints
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderDto orderDto = orderService.placeOrder(email, request);
        return new ResponseEntity<>(ApiResponse.success("Order placed successfully", orderDto), HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<OrderDto> orders = orderService.getMyOrders(email);
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", orders));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderDetail(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderDto order = orderService.getOrderDetail(email, id);
        return ResponseEntity.ok(ApiResponse.success("Order details fetched successfully", order));
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderDto order = orderService.cancelOrder(email, id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }

    // Admin Endpoints
    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getAllOrders(Pageable pageable) {
        Page<OrderDto> orders = orderService.getAllOrdersForAdmin(pageable);
        return ResponseEntity.ok(ApiResponse.success("Admin: All orders fetched successfully", orders));
    }

    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderDto order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Admin: Order status updated successfully", order));
    }
}
