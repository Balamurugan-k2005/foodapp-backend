package com.ecommerce.app.service;

import com.ecommerce.app.dto.CartDto;
import com.ecommerce.app.dto.CartItemRequest;
import com.ecommerce.app.entity.Cart;
import com.ecommerce.app.entity.CartItem;
import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.InsufficientStockException;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.CartMapper;
import com.ecommerce.app.repository.CartItemRepository;
import com.ecommerce.app.repository.CartRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    public CartDto getCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        Cart cart = getOrCreateCart(user);
        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartDto addItemToCart(String email, CartItemRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (!product.isActive()) {
            throw new RuntimeException("This product is no longer active and cannot be added to cart");
        }

        Cart cart = getOrCreateCart(user);

        // Check if item already exists in cart
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        int targetQuantity = request.getQuantity();
        if (cartItem != null) {
            targetQuantity += cartItem.getQuantity();
        }

        // Validate stock availability
        if (product.getStock() < targetQuantity) {
            throw new InsufficientStockException("Insufficient stock available for " + product.getName() + ". Available: " + product.getStock());
        }

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
        } else {
            cartItem.setQuantity(targetQuantity);
        }

        cartItemRepository.save(cartItem);
        
        // Refresh the cart relations
        return getCart(email);
    }

    @Transactional
    public CartDto updateItemQuantity(String email, Long productId, Integer quantity) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found inside this shopping cart"));

        if (quantity <= 0) {
            cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
            cartItemRepository.delete(cartItem);
        } else {
            if (product.getStock() < quantity) {
                throw new InsufficientStockException("Insufficient stock available for " + product.getName() + ". Available: " + product.getStock());
            }
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return getCart(email);
    }

    @Transactional
    public CartDto removeItemFromCart(String email, Long productId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found inside this shopping cart"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartItemRepository.delete(cartItem);
        return getCart(email);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCart(cart);
    }
}
