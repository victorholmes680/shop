package com.shopmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private Integer itemCount;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal total;

    public static CartResponse fromEntity(com.shopmall.entity.Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());

        BigDecimal subtotal = cart.getTotalPrice();
        BigDecimal taxAmount = subtotal.multiply(new BigDecimal("0.1")); // 10% tax
        BigDecimal shippingAmount = subtotal.compareTo(new BigDecimal("100")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("9.99");
        BigDecimal total = subtotal.add(taxAmount).add(shippingAmount);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .itemCount(itemResponses.stream()
                        .mapToInt(CartItemResponse::getQuantity)
                        .sum())
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .shippingAmount(shippingAmount)
                .total(total)
                .build();
    }
}
