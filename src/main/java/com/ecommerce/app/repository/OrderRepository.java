package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Order;
import com.ecommerce.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByRestaurantOrderByCreatedAtDesc(com.ecommerce.app.entity.Restaurant restaurant);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != com.ecommerce.app.entity.OrderStatus.CANCELLED AND o.status != com.ecommerce.app.entity.OrderStatus.REJECTED")
    java.math.BigDecimal calculateTotalRevenue();
}
