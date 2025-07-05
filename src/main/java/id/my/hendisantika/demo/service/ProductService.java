package id.my.hendisantika.demo.service;

import id.my.hendisantika.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for Product operations.
 */
public interface ProductService {

    /**
     * Save a product.
     */
    Product saveProduct(Product product);

    /**
     * Find product by ID.
     */
    Optional<Product> findById(Long id);

    /**
     * Find product by name.
     */
    Optional<Product> findByName(String name);

    /**
     * Find all products with pagination.
     */
    Page<Product> findAllProducts(Pageable pageable);

    /**
     * Find products by category with pagination.
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * Find products by price range.
     */
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products with low stock asynchronously.
     */
    CompletableFuture<List<Product>> findLowStockProductsAsync(Integer threshold);

    /**
     * Update product stock.
     */
    Product updateStock(Long productId, Integer newStock);

    /**
     * Delete product by ID.
     */
    void deleteProduct(Long id);

    /**
     * Count products by category.
     */
    long countByCategory(String category);

    /**
     * Bulk save products for better performance.
     */
    List<Product> saveAllProducts(List<Product> products);
}