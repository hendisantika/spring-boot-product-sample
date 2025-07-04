package com.example.demo.service.impl;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of ProductService with optimizations for high performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(LocalDateTime.now());
        }
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> findById(Long id) {
        log.debug("Finding product by ID: {}", id);
        return productRepository.findById(id);
    }

    @Override
    @Cacheable(value = "productsByName", key = "#name")
    public Optional<Product> findByName(String name) {
        log.debug("Finding product by name: {}", name);
        return productRepository.findByName(name);
    }

    @Override
    @Cacheable(value = "allProducts", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Product> findAllProducts(Pageable pageable) {
        log.debug("Finding all products with pagination: {}", pageable);
        return productRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "productsByCategory", key = "#category + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Product> findByCategory(String category, Pageable pageable) {
        log.debug("Finding products by category: {} with pagination: {}", category, pageable);
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    @Cacheable(value = "productsByPriceRange", key = "#minPrice.toString() + '_' + #maxPrice.toString()")
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Finding products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    @Async
    public CompletableFuture<List<Product>> findLowStockProductsAsync(Integer threshold) {
        log.debug("Finding low stock products asynchronously with threshold: {}", threshold);
        return CompletableFuture.completedFuture(productRepository.findLowStockProducts(threshold));
    }

    @Override
    @Transactional
    @CachePut(value = "products", key = "#productId")
    public Product updateStock(Long productId, Integer newStock) {
        log.debug("Updating stock for product ID: {} to {}", productId, newStock);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "productsByName", "allProducts", "productsByCategory", "productsByPriceRange"},
            key = "#id", allEntries = true)
    public void deleteProduct(Long id) {
        log.debug("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "productCountByCategory", key = "#category")
    public long countByCategory(String category) {
        log.debug("Counting products by category: {}", category);
        return productRepository.countByCategory(category);
    }

    @Override
    @Transactional
    public List<Product> saveAllProducts(List<Product> products) {
        log.debug("Bulk saving {} products", products.size());
        LocalDateTime now = LocalDateTime.now();
        products.forEach(product -> {
            if (product.getCreatedAt() == null) {
                product.setCreatedAt(now);
            }
            product.setUpdatedAt(now);
        });
        return productRepository.saveAll(products);
    }
}