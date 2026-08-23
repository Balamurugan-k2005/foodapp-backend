package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ApiResponse;
import com.ecommerce.app.dto.CartDto;
import com.ecommerce.app.dto.CartItemRequest;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDto cartDto = cartService.getCart(email);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cartDto));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDto cartDto = cartService.addItemToCart(email, request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", cartDto));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> updateItemQuantity(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDto cartDto = cartService.updateItemQuantity(email, productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated successfully", cartDto));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItemFromCart(@PathVariable Long productId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDto cartDto = cartService.removeItemFromCart(email, productId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", cartDto));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        cartService.clearCart(user);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", "All items cleared"));
    }
}
