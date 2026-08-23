package com.ecommerce.app.controller;

import com.ecommerce.app.dto.ApiResponse;
import com.ecommerce.app.dto.RestaurantDto;
import com.ecommerce.app.dto.RestaurantSaveRequest;
import com.ecommerce.app.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RestaurantDto>>> getRestaurants(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<RestaurantDto> restaurants = restaurantService.getRestaurants(search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Restaurants fetched successfully", restaurants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantDto>> getRestaurantById(@PathVariable Long id) {
        RestaurantDto restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant fetched successfully", restaurant));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<RestaurantDto>> getRestaurantByOwner() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        RestaurantDto restaurant = restaurantService.getRestaurantByOwnerEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Owner's restaurant fetched successfully", restaurant));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RestaurantDto>> saveRestaurant(@Valid @RequestBody RestaurantSaveRequest request) {
        RestaurantDto saved = restaurantService.saveRestaurant(request);
        return new ResponseEntity<>(ApiResponse.success("Restaurant created successfully", saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<RestaurantDto>> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantSaveRequest request) {
        RestaurantDto updated = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", updated));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANT_OWNER')")
    public ResponseEntity<ApiResponse<RestaurantDto>> toggleRestaurantStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        RestaurantDto updated = restaurantService.toggleRestaurantStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("Restaurant status toggled successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant deleted successfully", "Deleted restaurant with id: " + id));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RestaurantDto>>> getAllRestaurantsForAdmin(Pageable pageable) {
        Page<RestaurantDto> restaurants = restaurantService.getAllRestaurantsForAdmin(pageable);
        return ResponseEntity.ok(ApiResponse.success("All restaurants fetched successfully for admin", restaurants));
    }
}
