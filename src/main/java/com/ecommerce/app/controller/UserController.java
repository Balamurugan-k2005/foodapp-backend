package com.ecommerce.app.controller;

import com.ecommerce.app.dto.AddressDto;
import com.ecommerce.app.dto.ApiResponse;
import com.ecommerce.app.dto.ProfileUpdateRequest;
import com.ecommerce.app.dto.UserDto;
import com.ecommerce.app.service.AddressService;
import com.ecommerce.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto profile = userService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(@Valid @RequestBody AddressDto addressDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AddressDto added = addressService.addAddress(email, addressDto);
        return new ResponseEntity<>(ApiResponse.success("Address added successfully", added), HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressDto>>> getAddresses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<AddressDto> list = addressService.getAddresses(email);
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched successfully", list));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<AddressDto>> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDto addressDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AddressDto updated = addressService.updateAddress(email, id, addressDto);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", updated));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        addressService.deleteAddress(email, id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", "Deleted address with id: " + id));
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<UserDto>>> getAllUsers(org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Admin: Users fetched successfully", users));
    }

    @PutMapping("/{id}/role")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        UserDto updated = userService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.success("Admin: User role updated successfully", updated));
    }
}
