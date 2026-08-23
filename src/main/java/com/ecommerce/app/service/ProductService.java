package com.ecommerce.app.service;

import com.ecommerce.app.dto.ProductDto;
import com.ecommerce.app.dto.ProductSaveRequest;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.ProductMapper;
import com.ecommerce.app.repository.CategoryRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final com.ecommerce.app.repository.RestaurantRepository restaurantRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductMapper productMapper,
                          com.ecommerce.app.repository.RestaurantRepository restaurantRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.restaurantRepository = restaurantRepository;
    }

    public Page<ProductDto> getProducts(String categorySlug,
                                        Long restaurantId,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice,
                                        String search,
                                        Boolean activeOnly,
                                        Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(categorySlug))
                .and(ProductSpecification.hasRestaurant(restaurantId))
                .and(ProductSpecification.isPriceGreaterThanOrEqual(minPrice))
                .and(ProductSpecification.isPriceLessThanOrEqual(maxPrice))
                .and(ProductSpecification.searchKeyword(search))
                .and(ProductSpecification.isActive(activeOnly));

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(productMapper::toDto);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDto saveProduct(ProductSaveRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        com.ecommerce.app.entity.Restaurant restaurant = null;
        if (request.getRestaurantId() != null) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));
        }

        String slug = generateSlug(request.getName());
        if (productRepository.existsBySlug(slug)) {
            // Add unique differentiator if duplicate slug
            slug = slug + "-" + System.currentTimeMillis();
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .slug(slug)
                .category(category)
                .restaurant(restaurant)
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductSaveRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        com.ecommerce.app.entity.Restaurant restaurant = null;
        if (request.getRestaurantId() != null) {
            restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + request.getRestaurantId()));
        }

        product.setName(request.getName());
        product.setSlug(generateSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setRestaurant(restaurant);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Soft delete
        product.setActive(false);
        productRepository.save(product);
    }

    private String generateSlug(String input) {
        if (input == null) return null;
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }
}
