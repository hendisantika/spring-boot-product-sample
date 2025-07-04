package com.example.demo.repository;

import com.example.demo.model.Product;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entity with optimized query methods for high performance.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by name with query cache enabled.
     */
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    Optional<Product> findByName(String name);

    /**
     * Find products by category with pagination support.
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * Find products by price range with optimized query.
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price")
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find products with stock below threshold.
     */
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    /**
     * Count products by category.
     */
    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
    long countByCategory(String category);
}