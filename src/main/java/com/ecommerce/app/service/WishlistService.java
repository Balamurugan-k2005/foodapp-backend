package com.ecommerce.app.service;

import com.ecommerce.app.dto.WishlistDto;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.entity.Wishlist;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.WishlistMapper;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistMapper wishlistMapper;

    public WishlistService(WishlistRepository wishlistRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           WishlistMapper wishlistMapper) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.wishlistMapper = wishlistMapper;
    }

    private Wishlist getOrCreateWishlist(User user) {
        return wishlistRepository.findByUser(user)
                .orElseGet(() -> {
                    Wishlist wishlist = Wishlist.builder().user(user).build();
                    return wishlistRepository.save(wishlist);
                });
    }

    public WishlistDto getWishlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        Wishlist wishlist = getOrCreateWishlist(user);
        return wishlistMapper.toDto(wishlist);
    }

    @Transactional
    public WishlistDto toggleWishlistItem(String email, Long productId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Wishlist wishlist = getOrCreateWishlist(user);

        boolean exists = wishlist.getProducts().stream()
                .anyMatch(p -> p.getId().equals(productId));

        if (exists) {
            wishlist.getProducts().removeIf(p -> p.getId().equals(productId));
        } else {
            wishlist.getProducts().add(product);
        }

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.toDto(savedWishlist);
    }
}
