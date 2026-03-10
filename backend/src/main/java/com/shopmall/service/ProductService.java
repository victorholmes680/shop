package com.shopmall.service;

import com.shopmall.dto.ProductRequest;
import com.shopmall.dto.ProductResponse;
import com.shopmall.dto.ProductSearchRequest;
import com.shopmall.entity.Category;
import com.shopmall.entity.Product;
import com.shopmall.exception.BadRequestException;
import com.shopmall.exception.ResourceNotFoundException;
import com.shopmall.repository.CategoryRepository;
import com.shopmall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Search products with filters and pagination
     */
    public Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest) {
        Sort sort = createSort(searchRequest.getSortBy(), searchRequest.getSortDirection());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);

        Page<Product> products;

        // Build query based on filters
        if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().isBlank()) {
            // Search by keyword
            products = productRepository.searchProducts(searchRequest.getKeyword(), pageable);
        } else if (searchRequest.getCategoryId() != null) {
            // Filter by category
            if (searchRequest.getMinPrice() != null || searchRequest.getMaxPrice() != null) {
                BigDecimal minPrice = searchRequest.getMinPrice() != null ? searchRequest.getMinPrice() : BigDecimal.ZERO;
                BigDecimal maxPrice = searchRequest.getMaxPrice() != null ? searchRequest.getMaxPrice() : new BigDecimal("999999.99");
                products = productRepository.findByCategoryAndPriceRange(
                        searchRequest.getCategoryId(), minPrice, maxPrice, pageable);
            } else {
                products = productRepository.findByCategory_IdAndActiveTrue(searchRequest.getCategoryId(), pageable);
            }
        } else if (searchRequest.getFeatured() != null && searchRequest.getFeatured()) {
            // Get featured products
            products = productRepository.findByFeaturedTrueAndActiveTrue().stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())));
        } else {
            // Get all active products
            products = productRepository.findByActiveTrue(pageable);
        }

        return products.map(ProductResponse::fromEntity);
    }

    /**
     * Get product by ID
     */
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductResponse.fromEntity(product);
    }

    /**
     * Get featured products
     */
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get products by category
     */
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        return productRepository.findByCategoryAndActiveTrue(category, pageable)
                .map(ProductResponse::fromEntity);
    }

    /**
     * Create new product (admin only)
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(category)
                .active(true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .build();

        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    /**
     * Update product (admin only)
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }

        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    /**
     * Delete product (admin only)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
    }

    /**
     * Toggle product active status (admin only)
     */
    @Transactional
    public ProductResponse toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setActive(!product.getActive());
        product = productRepository.save(product);

        return ProductResponse.fromEntity(product);
    }

    /**
     * Update stock quantity (admin only)
     */
    @Transactional
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (quantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }

        product.setStockQuantity(quantity);
        product = productRepository.save(product);

        return ProductResponse.fromEntity(product);
    }

    /**
     * Get low stock products (admin only)
     */
    public List<ProductResponse> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Create Sort object from sortBy and sortDirection strings
     */
    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // Validate sortBy field
        List<String> allowedFields = List.of("name", "price", "createdAt", "stockQuantity");
        String field = (sortBy != null && allowedFields.contains(sortBy)) ? sortBy : "createdAt";

        return Sort.by(direction, field);
    }
}
