package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Restaurant;
import com.ecommerce.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByOwner(User owner);
    Page<Restaurant> findByIsActiveTrue(Pageable pageable);
    Page<Restaurant> findByIsActiveTrueAndNameContainingIgnoreCaseOrIsActiveTrueAndCuisineTypeContainingIgnoreCase(String name, String cuisine, Pageable pageable);
    long countByIsActiveTrue();
}
