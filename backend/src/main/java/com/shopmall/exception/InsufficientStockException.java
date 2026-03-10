package com.shopmall.exception;

public class InsufficientStockException extends RuntimeException {

    private final Long productId;
    private final String productName;
    private final Integer requested;
    private final Integer available;

    public InsufficientStockException(Long productId, String productName, Integer requested, Integer available) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                productName, requested, available));
        this.productId = productId;
        this.productName = productName;
        this.requested = requested;
        this.available = available;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getRequested() {
        return requested;
    }

    public Integer getAvailable() {
        return available;
    }
}
