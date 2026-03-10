package com.shopmall.service;

import com.shopmall.dto.CartItemRequest;
import com.shopmall.dto.CartItemResponse;
import com.shopmall.dto.CartResponse;
import com.shopmall.dto.UpdateCartItemRequest;
import com.shopmall.entity.Cart;
import com.shopmall.entity.CartItem;
import com.shopmall.entity.Product;
import com.shopmall.entity.User;
import com.shopmall.exception.BadRequestException;
import com.shopmall.exception.InsufficientStockException;
import com.shopmall.exception.ResourceNotFoundException;
import com.shopmall.exception.UnauthorizedException;
import com.shopmall.repository.CartRepository;
import com.shopmall.repository.CartItemRepository;
import com.shopmall.repository.ProductRepository;
import com.shopmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Get user's cart
     */
    public CartResponse getUserCart(String userEmail) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);
        return CartResponse.fromEntity(cart);
    }

    /**
     * Add item to cart
     */
    @Transactional
    public CartResponse addItem(String userEmail, CartItemRequest request) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // Check if product is active
        if (!product.getActive()) {
            throw new BadRequestException("Product is not available");
        }

        // Check stock availability
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    product.getId(),
                    product.getName(),
                    request.getQuantity(),
                    product.getStockQuantity()
            );
        }

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (existingItem != null) {
            // Update quantity if item exists
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException(
                        product.getId(),
                        product.getName(),
                        newQuantity,
                        product.getStockQuantity()
                );
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        return CartResponse.fromEntity(cart);
    }

    /**
     * Update cart item quantity
     */
    @Transactional
    public CartResponse updateItemQuantity(String userEmail, Long itemId, UpdateCartItemRequest request) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", itemId));

        // Verify item belongs to user's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Cart item does not belong to your cart");
        }

        if (request.getQuantity() == 0) {
            // Remove item if quantity is 0
            cartItemRepository.delete(item);
        } else {
            // Check stock availability
            Product product = item.getProduct();
            if (product.getStockQuantity() < request.getQuantity()) {
                throw new InsufficientStockException(
                        product.getId(),
                        product.getName(),
                        request.getQuantity(),
                        product.getStockQuantity()
                );
            }
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }

        return CartResponse.fromEntity(cart);
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public CartResponse removeItem(String userEmail, Long itemId) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", itemId));

        // Verify item belongs to user's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Cart item does not belong to your cart");
        }

        cartItemRepository.delete(item);

        return CartResponse.fromEntity(cart);
    }

    /**
     * Clear cart
     */
    @Transactional
    public void clearCart(String userEmail) {
        User user = getUserByEmail(userEmail);
        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteByCart(cart);
        cart.getItems().clear();
    }

    /**
     * Get cart item count
     */
    public Integer getCartItemCount(String userEmail) {
        User user = getUserByEmail(userEmail);
        Cart cart = cartRepository.findByUserIdWithItems(user.getId()).orElse(null);

        if (cart == null || cart.getItems().isEmpty()) {
            return 0;
        }

        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Get or create cart for user
     */
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserIdWithItems(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Get user by email
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
