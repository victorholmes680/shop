package com.shopmall.service;

import com.shopmall.dto.CreateOrderRequest;
import com.shopmall.dto.OrderResponse;
import com.shopmall.dto.UpdateOrderStatusRequest;
import com.shopmall.entity.*;
import com.shopmall.exception.BadRequestException;
import com.shopmall.exception.ResourceNotFoundException;
import com.shopmall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final PaymentService paymentService;

  /**
   * Create order from user's cart
   */
  @Transactional
  public OrderResponse createOrder(String userEmail, CreateOrderRequest request) {
    User user = getUserByEmail(userEmail);

    // Get user's cart
    Cart cart = cartRepository.findByUserIdWithItems(user.getId())
        .orElseThrow(() -> new BadRequestException("Your cart is empty"));

    if (cart.getItems().isEmpty()) {
      throw new BadRequestException("Your cart is empty");
    }

    // Check stock availability
    for (CartItem item : cart.getItems()) {
      Product product = item.getProduct();
      if (product.getStockQuantity() < item.getQuantity()) {
        throw new BadRequestException("Not enough stock for product: " + product.getName());
      }
    }

    // Calculate totals
    BigDecimal subtotal = cart.getTotalPrice();
    BigDecimal taxAmount = subtotal.multiply(new BigDecimal("0.10")); // 10% tax
    BigDecimal shippingAmount = subtotal.compareTo(new BigDecimal("100")) >= 0
        ? BigDecimal.ZERO
        : new BigDecimal("9.99");
    BigDecimal totalAmount = subtotal.add(taxAmount).add(shippingAmount);

    // Create order
    Order order = Order.builder()
        .user(user)
        .status(OrderStatus.PENDING)
        .totalAmount(totalAmount)
        .taxAmount(taxAmount)
        .shippingAmount(shippingAmount)
        .shippingAddress(request.getShippingAddress())
        .shippingCity(request.getShippingCity())
        .shippingPostalCode(request.getShippingPostalCode())
        .shippingPhone(request.getShippingPhone())
        .paymentMethod(request.getPaymentMethod())
        .paymentStatus(PaymentStatus.PENDING)
        .notes(request.getNotes())
        .items(new ArrayList<>())
        .build();

    order = orderRepository.save(order);

    // Create order items
    for (CartItem cartItem : cart.getItems()) {
      Product product = cartItem.getProduct();

      OrderItem orderItem = OrderItem.builder()
          .order(order)
          .product(product)
          .productName(product.getName())
          .unitPrice(product.getPrice())
          .quantity(cartItem.getQuantity())
          .subtotal(cartItem.getSubtotal())
          .build();

      orderItemRepository.save(orderItem);
      order.getItems().add(orderItem);

      // Update product stock
      product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
      productRepository.save(product);
    }

    // Process payment
    PaymentService.PaymentResult paymentResult = paymentService.processPayment(order, request.getPaymentMethod());

    if (paymentResult.isSuccess()) {
      order.setPaymentStatus(PaymentStatus.COMPLETED);
      order.setStatus(OrderStatus.CONFIRMED);
    } else {
      order.setPaymentStatus(PaymentStatus.FAILED);
      order.setStatus(OrderStatus.CANCELLED);
    }

    order = orderRepository.save(order);

    // Clear cart only after successful payment
    if (paymentResult.isSuccess()) {
      cart.getItems().clear();
      cartRepository.save(cart);
    }

    return OrderResponse.fromEntity(order);
  }

  /**
   * Get order by ID
   */
  public OrderResponse getOrderById(Long id, String userEmail) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

    // Check if user owns the order or is admin
    if (!order.getUser().getEmail().equals(userEmail)) {
      throw new BadRequestException("You do not have permission to view this order");
    }

    return OrderResponse.fromEntity(order);
  }

  /**
   * Get order by order number
   */
  public OrderResponse getOrderByOrderNumber(String orderNumber, String userEmail) {
    Order order = orderRepository.findByOrderNumber(orderNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

    // Check if user owns the order
    if (!order.getUser().getEmail().equals(userEmail)) {
      throw new BadRequestException("You do not have permission to view this order");
    }

    return OrderResponse.fromEntity(order);
  }

  /**
   * Get user's orders with pagination
   */
  public Page<OrderResponse> getUserOrders(String userEmail, Pageable pageable) {
    User user = getUserByEmail(userEmail);
    return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable)
        .map(OrderResponse::fromEntity);
  }

  /**
   * Get all orders (admin only)
   */
  public Page<OrderResponse> getAllOrders(Pageable pageable) {
    return orderRepository.findAll(pageable)
        .map(OrderResponse::fromEntity);
  }

  /**
   * Get orders by status (admin only)
   */
  public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
    return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
        .map(OrderResponse::fromEntity);
  }

  /**
   * Update order status (admin only)
   */
  @Transactional
  public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

    order.setStatus(request.getStatus());
    order = orderRepository.save(order);

    return OrderResponse.fromEntity(order);
  }

  /**
   * Cancel order
   */
  @Transactional
  public OrderResponse cancelOrder(Long id, String userEmail) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

    // Check if user owns the order
    if (!order.getUser().getEmail().equals(userEmail)) {
      throw new BadRequestException("You do not have permission to cancel this order");
    }

    // Can only cancel pending or confirmed orders
    if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
      throw new BadRequestException("Cannot cancel order with status: " + order.getStatus());
    }

    order.setStatus(OrderStatus.CANCELLED);

    // Refund items to stock
    for (OrderItem item : order.getItems()) {
      Product product = item.getProduct();
      product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
      productRepository.save(product);
    }

    // Process refund if payment was completed
    if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
      paymentService.refundPayment(order);
      order.setPaymentStatus(PaymentStatus.REFUNDED);
    }

    order = orderRepository.save(order);

    return OrderResponse.fromEntity(order);
  }

  /**
   * Get order statistics (admin only)
   */
  public OrderStatistics getOrderStatistics() {
    OrderStatistics stats = new OrderStatistics();
    stats.setTotalOrders(orderRepository.count());
    stats.setPendingOrders(orderRepository.countByStatus(OrderStatus.PENDING));
    stats.setConfirmedOrders(orderRepository.countByStatus(OrderStatus.CONFIRMED));
    stats.setProcessingOrders(orderRepository.countByStatus(OrderStatus.PROCESSING));
    stats.setShippedOrders(orderRepository.countByStatus(OrderStatus.SHIPPED));
    stats.setDeliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED));
    stats.setCancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED));

    return stats;
  }

  /**
   * Get user by email
   */
  private User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
  }

  /**
   * Order statistics class
   */
  public static class OrderStatistics {
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long processingOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;

    public long getTotalOrders() {
      return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
      this.totalOrders = totalOrders;
    }

    public long getPendingOrders() {
      return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
      this.pendingOrders = pendingOrders;
    }

    public long getConfirmedOrders() {
      return confirmedOrders;
    }

    public void setConfirmedOrders(long confirmedOrders) {
      this.confirmedOrders = confirmedOrders;
    }

    public long getProcessingOrders() {
      return processingOrders;
    }

    public void setProcessingOrders(long processingOrders) {
      this.processingOrders = processingOrders;
    }

    public long getShippedOrders() {
      return shippedOrders;
    }

    public void setShippedOrders(long shippedOrders) {
      this.shippedOrders = shippedOrders;
    }

    public long getDeliveredOrders() {
      return deliveredOrders;
    }

    public void setDeliveredOrders(long deliveredOrders) {
      this.deliveredOrders = deliveredOrders;
    }

    public long getCancelledOrders() {
      return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
      this.cancelledOrders = cancelledOrders;
    }
  }
}
