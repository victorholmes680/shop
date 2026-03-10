package com.shopmall.service;

import com.shopmall.dto.CategoryRequest;
import com.shopmall.dto.CategoryResponse;
import com.shopmall.entity.Category;
import com.shopmall.entity.Product;
import com.shopmall.exception.BadRequestException;
import com.shopmall.exception.ResourceNotFoundException;
import com.shopmall.repository.CategoryRepository;
import com.shopmall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Get all categories (root level only)
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllRootCategories().stream()
                .map(CategoryResponse::fromEntityWithSubCategories)
                .collect(Collectors.toList());
    }

    /**
     * Get all active categories
     */
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrue().stream()
                .map(CategoryResponse::fromEntityWithSubCategories)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return CategoryResponse.fromEntityWithSubCategories(category);
    }

    /**
     * Create new category
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Check if category name already exists
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Category with this name already exists");
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category", "id", request.getParentId()));
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .parent(parent)
                .active(true)
                .build();

        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    /**
     * Update category
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Check if name is being changed and if new name already exists
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Category with this name already exists");
        }

        // Update parent if provided
        Category parent = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be its own parent");
            }
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category", "id", request.getParentId()));
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setParent(parent);

        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    /**
     * Delete category (soft delete by setting active to false)
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Check if category has products
        List<Product> products = productRepository.findByCategoryAndActiveTrue(category, org.springframework.data.domain.Pageable.unpaged()).getContent();
        if (!products.isEmpty()) {
            throw new BadRequestException("Cannot delete category with existing products");
        }

        // Check if category has subcategories
        if (!category.getSubCategories().isEmpty()) {
            throw new BadRequestException("Cannot delete category with existing subcategories");
        }

        categoryRepository.delete(category);
    }

    /**
     * Toggle category active status
     */
    @Transactional
    public CategoryResponse toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setActive(!category.getActive());
        category = categoryRepository.save(category);

        return CategoryResponse.fromEntity(category);
    }
}
