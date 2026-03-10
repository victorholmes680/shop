package com.shopmall.service;

import com.shopmall.entity.Order;
import com.shopmall.entity.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@Slf4j
public class PaymentService {

    private final Random random = new Random();

    /**
     * Process payment for an order (mock implementation)
     * In production, this would integrate with a real payment gateway like Stripe, PayPal, etc.
     */
    public PaymentResult processPayment(Order order, String paymentMethod) {
        log.info("Processing payment for order: {} using method: {}", order.getOrderNumber(), paymentMethod);

        // Simulate payment processing
        // In real implementation, this would call external payment gateway APIs
        boolean success = simulatePaymentGateway();

        PaymentResult result = new PaymentResult();
        result.setSuccess(success);
        result.setTransactionId(generateTransactionId());

        if (success) {
            result.setMessage("Payment processed successfully");
            log.info("Payment successful for order: {}, Transaction ID: {}", order.getOrderNumber(), result.getTransactionId());
        } else {
            result.setMessage("Payment failed. Please try again.");
            log.warn("Payment failed for order: {}", order.getOrderNumber());
        }

        return result;
    }

    /**
     * Refund payment for an order (mock implementation)
     */
    public PaymentResult refundPayment(Order order) {
        log.info("Processing refund for order: {}", order.getOrderNumber());

        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setTransactionId(generateTransactionId());
        result.setMessage("Refund processed successfully");

        log.info("Refund successful for order: {}, Transaction ID: {}", order.getOrderNumber(), result.getTransactionId());

        return result;
    }

    /**
     * Simulate payment gateway response
     * 90% success rate for demo purposes
     */
    private boolean simulatePaymentGateway() {
        return random.nextInt(100) < 90;
    }

    /**
     * Generate mock transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + random.nextInt(10000);
    }

    /**
     * Payment result class
     */
    public static class PaymentResult {
        private boolean success;
        private String transactionId;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
