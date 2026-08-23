package com.ecommerce.app.service;

import com.ecommerce.app.dto.ReviewDto;
import com.ecommerce.app.dto.ReviewSaveRequest;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.ReviewMapper;
import com.ecommerce.app.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         UserRepository userRepository,
                         OrderRepository orderRepository,
                         ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public ReviewDto addReview(String email, Long productId, ReviewSaveRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if user has already reviewed the product
        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("You have already reviewed this product");
        }

        // Validate that user has purchased the product
        boolean hasPurchased = orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .anyMatch(order -> order.getItems().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(productId)));

        if (!hasPurchased) {
            throw new RuntimeException("Only customers who purchased this product can leave a review");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        
        // Refresh product collections to re-average scores
        product.getReviews().add(savedReview);
        productRepository.save(product);

        return reviewMapper.toDto(savedReview);
    }

    public Page<ReviewDto> getReviewsByProduct(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return reviewRepository.findByProductId(productId, pageable)
                .map(reviewMapper::toDto);
    }
}
