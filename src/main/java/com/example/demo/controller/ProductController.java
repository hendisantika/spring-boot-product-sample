package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for Product operations optimized for high performance.
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    /**
     * Create a new product.
     */
    @Operation(summary = "Create a new product", description = "Creates a new product with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Product object to be created", required = true) @RequestBody Product product) {
        log.info("Creating new product: {}", product.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(product));
    }

    /**
     * Get product by ID.
     */
    @Operation(summary = "Get a product by ID", description = "Returns a product based on the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true) @PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get product by name.
     */
    @Operation(summary = "Get a product by name", description = "Returns a product based on the provided name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<Product> getProductByName(
            @Parameter(description = "Name of the product to retrieve", required = true) @PathVariable String name) {
        log.info("Fetching product with name: {}", name);
        return productService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all products with pagination.
     */
    @Operation(summary = "Get all products", description = "Returns a paginated list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Size of each page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sort) {
        log.info("Fetching all products - page: {}, size: {}, sort: {}", page, size, sort);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(productService.findAllProducts(pageRequest));
    }

    /**
     * Get products by category with pagination.
     */
    @Operation(summary = "Get products by category", description = "Returns a paginated list of products in the specified category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @Parameter(description = "Category to filter by", required = true) @PathVariable String category,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Size of each page") @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching products by category: {} - page: {}, size: {}", category, page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.findByCategory(category, pageRequest));
    }

    /**
     * Get products by price range.
     */
    @Operation(summary = "Get products by price range", description = "Returns a list of products within the specified price range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @Parameter(description = "Minimum price", required = true) @RequestParam BigDecimal min,
            @Parameter(description = "Maximum price", required = true) @RequestParam BigDecimal max) {
        log.info("Fetching products by price range: {} - {}", min, max);
        return ResponseEntity.ok(productService.findByPriceRange(min, max));
    }

    /**
     * Get low stock products asynchronously.
     */
    @Operation(summary = "Get low stock products", description = "Returns a list of products with stock below the specified threshold (asynchronous)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/low-stock/{threshold}")
    public CompletableFuture<ResponseEntity<List<Product>>> getLowStockProducts(
            @Parameter(description = "Stock threshold", required = true) @PathVariable Integer threshold) {
        log.info("Fetching low stock products with threshold: {}", threshold);
        return productService.findLowStockProductsAsync(threshold)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Update product stock.
     */
    @Operation(summary = "Update product stock", description = "Updates the stock quantity for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PatchMapping("/{id}/stock/{stock}")
    public ResponseEntity<Product> updateProductStock(
            @Parameter(description = "ID of the product to update", required = true) @PathVariable Long id,
            @Parameter(description = "New stock quantity", required = true) @PathVariable Integer stock) {
        log.info("Updating stock for product ID: {} to {}", id, stock);
        return ResponseEntity.ok(productService.updateStock(id, stock));
    }
}
