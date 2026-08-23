package com.ecommerce.app.service;

import com.ecommerce.app.dto.RestaurantDto;
import com.ecommerce.app.dto.RestaurantSaveRequest;
import com.ecommerce.app.entity.Restaurant;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.repository.RestaurantRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public Page<RestaurantDto> getRestaurants(String search, Pageable pageable) {
        Page<Restaurant> page;
        if (search != null && !search.trim().isEmpty()) {
            page = restaurantRepository.findByIsActiveTrueAndNameContainingIgnoreCaseOrIsActiveTrueAndCuisineTypeContainingIgnoreCase(search, search, pageable);
        } else {
            page = restaurantRepository.findByIsActiveTrue(pageable);
        }
        return page.map(this::toDto);
    }

    public Page<RestaurantDto> getAllRestaurantsForAdmin(Pageable pageable) {
        return restaurantRepository.findAll(pageable).map(this::toDto);
    }

    public RestaurantDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        return toDto(restaurant);
    }

    public RestaurantDto getRestaurantByOwnerEmail(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        Restaurant restaurant = restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant associated with this owner"));
        return toDto(restaurant);
    }

    @Transactional
    public RestaurantDto saveRestaurant(RestaurantSaveRequest request) {
        User owner = null;
        if (request.getOwnerId() != null) {
            owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .cuisineType(request.getCuisineType())
                .deliveryTime(request.getDeliveryTime() != null ? request.getDeliveryTime() : 30)
                .averagePrice(request.getAveragePrice())
                .isActive(true)
                .owner(owner)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        return toDto(saved);
    }

    @Transactional
    public RestaurantDto updateRestaurant(Long id, RestaurantSaveRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setCuisineType(request.getCuisineType());
        if (request.getDeliveryTime() != null) {
            restaurant.setDeliveryTime(request.getDeliveryTime());
        }
        restaurant.setAveragePrice(request.getAveragePrice());

        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            restaurant.setOwner(owner);
        }

        Restaurant updated = restaurantRepository.save(restaurant);
        return toDto(updated);
    }

    @Transactional
    public RestaurantDto toggleRestaurantStatus(Long id, boolean active) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        restaurant.setActive(active);
        Restaurant saved = restaurantRepository.save(restaurant);
        return toDto(saved);
    }

    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        restaurantRepository.delete(restaurant);
    }

    public RestaurantDto toDto(Restaurant restaurant) {
        if (restaurant == null) return null;
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .imageUrl(restaurant.getImageUrl())
                .cuisineType(restaurant.getCuisineType())
                .deliveryTime(restaurant.getDeliveryTime())
                .averagePrice(restaurant.getAveragePrice())
                .active(restaurant.isActive())
                .ownerId(restaurant.getOwner() != null ? restaurant.getOwner().getId() : null)
                .ownerName(restaurant.getOwner() != null ? restaurant.getOwner().getName() : null)
                .build();
    }
}
