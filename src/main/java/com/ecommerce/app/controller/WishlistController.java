package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ApiResponse;
import com.ecommerce.app.dto.WishlistDto;
import com.ecommerce.app.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistDto>> getWishlist() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        WishlistDto wishlist = wishlistService.getWishlist(email);
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully", wishlist));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistDto>> toggleWishlistItem(@PathVariable Long productId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        WishlistDto wishlist = wishlistService.toggleWishlistItem(email, productId);
        return ResponseEntity.ok(ApiResponse.success("Wishlist items updated successfully", wishlist));
    }
}
