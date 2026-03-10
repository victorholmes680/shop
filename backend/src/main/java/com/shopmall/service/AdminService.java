package com.shopmall.service;

import com.shopmall.dto.*;
import com.shopmall.entity.*;
import com.shopmall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Get complete dashboard statistics
     */
    public DashboardStatistics getDashboardStatistics() {
        // Overview stats
        Long totalUsers = userRepository.count();
        Long totalProducts = productRepository.count();
        Long totalOrders = orderRepository.count();

        // Calculate total revenue from completed/delivered orders
        List<Order> completedOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .collect(Collectors.toList());
        BigDecimal totalRevenue = completedOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Recent orders (last 10)
        List<Order> recentOrders = orderRepository.findTop10ByOrderByCreatedAtDesc();
        List<OrderResponse> recentOrderResponses = recentOrders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        // Low stock products (less than 10)
        List<Product> lowStockProducts = productRepository.findLowStockProducts(10);
        List<ProductResponse> lowStockProductResponses = lowStockProducts.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        // Top selling products (by quantity ordered)
        Map<Long, Integer> productSales = new HashMap<>();
        List<OrderItem> allOrderItems = orderItemRepository.findAll();
        for (OrderItem item : allOrderItems) {
            productSales.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
        }

        List<ProductResponse> topSellingProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> productRepository.findById(entry.getKey())
                        .map(ProductResponse::fromEntity)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Order status statistics
        DashboardStatistics.OrderStatusStatistics orderStats = DashboardStatistics.OrderStatusStatistics.builder()
                .pending(orderRepository.countByStatus(OrderStatus.PENDING))
                .confirmed(orderRepository.countByStatus(OrderStatus.CONFIRMED))
                .processing(orderRepository.countByStatus(OrderStatus.PROCESSING))
                .shipped(orderRepository.countByStatus(OrderStatus.SHIPPED))
                .delivered(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .cancelled(orderRepository.countByStatus(OrderStatus.CANCELLED))
                .build();

        // Sales data for the last 7 days
        List<DashboardStatistics.SalesDataPoint> salesData = getSalesData(7);

        return DashboardStatistics.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .recentOrders(recentOrderResponses)
                .lowStockProducts(lowStockProductResponses)
                .topSellingProducts(topSellingProducts)
                .orderStatusStatistics(orderStats)
                .salesData(salesData)
                .build();
    }

    /**
     * Get sales data for the last N days
     */
    private List<DashboardStatistics.SalesDataPoint> getSalesData(int days) {
        List<DashboardStatistics.SalesDataPoint> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            List<Order> ordersForDay = orderRepository.findAll().stream()
                    .filter(o -> !o.getCreatedAt().isBefore(startOfDay) && o.getCreatedAt().isBefore(endOfDay))
                    .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                    .collect(Collectors.toList());

            BigDecimal revenue = ordersForDay.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            data.add(DashboardStatistics.SalesDataPoint.builder()
                    .period(date.format(formatter))
                    .revenue(revenue)
                    .orderCount((long) ordersForDay.size())
                    .build());
        }

        return data;
    }

    /**
     * Get all users with pagination
     */
    public Map<String, Object> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<com.shopmall.entity.User> users = userRepository.findAll();

        List<UserSummaryResponse> userResponses = users.stream()
                .map(user -> {
                    long orderCount = orderRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged()).getTotalElements();
                    return UserSummaryResponse.fromEntity(user, orderCount);
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("users", userResponses);
        result.put("total", users.size());
        return result;
    }

    /**
     * Get user by ID
     */
    public UserSummaryResponse getUserById(Long id) {
        com.shopmall.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new com.shopmall.exception.ResourceNotFoundException("User", "id", id));

        long orderCount = orderRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged()).getTotalElements();

        return UserSummaryResponse.fromEntity(user, orderCount);
    }

    /**
     * Update user (admin only)
     */
    public UserSummaryResponse updateUser(Long id, UpdateUserRequest request) {
        com.shopmall.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new com.shopmall.exception.ResourceNotFoundException("User", "id", id));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(request.getRoles());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        user = userRepository.save(user);

        long orderCount = orderRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged()).getTotalElements();

        return UserSummaryResponse.fromEntity(user, orderCount);
    }

    /**
     * Delete user (admin only)
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new com.shopmall.exception.ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Toggle user status (active/inactive)
     */
    public UserSummaryResponse toggleUserStatus(Long id) {
        com.shopmall.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new com.shopmall.exception.ResourceNotFoundException("User", "id", id));

        user.setEnabled(!user.getEnabled());
        user = userRepository.save(user);

        long orderCount = orderRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged()).getTotalElements();

        return UserSummaryResponse.fromEntity(user, orderCount);
    }
}
