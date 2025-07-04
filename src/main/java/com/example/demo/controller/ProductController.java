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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
