package com.shopmall.repository;

import com.shopmall.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByParentIsNullAndActiveTrue();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.id = :id")
    Optional<Category> findByIdWithSubCategories(Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.parent IS NULL AND c.active = true")
    List<Category> findAllRootCategories();
}
