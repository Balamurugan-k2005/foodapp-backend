package com.ecommerce.app.service;

import com.ecommerce.app.dto.OrderDto;
import com.ecommerce.app.dto.PlaceOrderRequest;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.InsufficientStockException;
import com.ecommerce.app.exception.InvalidCouponException;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.mapper.OrderMapper;
import com.ecommerce.app.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        AddressRepository addressRepository,
                        CouponRepository couponRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        CartService cartService,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDto placeOrder(String email, PlaceOrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + request.getAddressId()));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Delivery address must belong to checkout user");
        }

        // Calculate Cart Totals & Stock Verification
        BigDecimal subtotal = BigDecimal.ZERO;
        List<CartItem> cartItems = cart.getItems();

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }

        com.ecommerce.app.entity.Restaurant restaurant = cartItems.get(0).getProduct().getRestaurant();
        if (restaurant == null) {
            throw new RuntimeException("Cart items must belong to a valid restaurant");
        }

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getRestaurant() == null || !product.getRestaurant().getId().equals(restaurant.getId())) {
                throw new RuntimeException("All items in the cart must belong to the same restaurant");
            }
            if (!product.isActive()) {
                throw new RuntimeException("Product " + product.getName() + " is no longer active");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product " + product.getName() + ". Available: " + product.getStock());
            }
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal finalTotal = subtotal;

        // Apply Coupon logic if provided
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new InvalidCouponException("Coupon code not found: " + request.getCouponCode()));

            if (!coupon.isActive()) {
                throw new InvalidCouponException("This coupon has been deactivated");
            }

            if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
                throw new InvalidCouponException("This coupon has expired");
            }

            if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
                throw new InvalidCouponException("Minimum order amount for coupon code is: " + coupon.getMinOrderAmount());
            }

            BigDecimal discountDecimal = BigDecimal.valueOf(coupon.getDiscountPercent()).divide(BigDecimal.valueOf(100));
            BigDecimal discountValue = subtotal.multiply(discountDecimal);
            finalTotal = subtotal.subtract(discountValue);
        }

        // Deduct inventory stock
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // Persist Order details
        Order order = Order.builder()
                .user(user)
                .address(address)
                .restaurant(restaurant)
                .status(OrderStatus.PENDING)
                .totalAmount(finalTotal)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .priceAtPurchase(item.getProduct().getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Clear shopping cart
        cartService.clearCart(user);

        return orderMapper.toDto(savedOrder);
    }

    public List<OrderDto> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderDetail(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Customers can check only their own orders. Admins can view any order.
        if (user.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized check of order details");
        }

        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(String email, Long orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized cancellation request");
        }

        // Verification of Cancellable statuses
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Cancellations are only allowed on pending or confirmed orders. Current: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Put stock back to the products
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    // Admin Operations
    public Page<OrderDto> getAllOrdersForAdmin(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }
}
