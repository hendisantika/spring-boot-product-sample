package id.my.hendisantika.demo.service.impl;

import id.my.hendisantika.demo.model.Product;
import id.my.hendisantika.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // Setup test data
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .category("Test Category")
                .price(new BigDecimal("99.99"))
                .stock(100)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
                .description("Test Description 2")
                .category("Test Category")
                .price(new BigDecimal("199.99"))
                .stock(50)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productList = Arrays.asList(testProduct, product2);
    }

    @Test
    void saveProduct_ShouldSetCreatedAtAndUpdatedAt_WhenCreatedAtIsNull() {
        // Arrange
        Product productToSave = Product.builder()
                .name("New Product")
                .description("New Description")
                .category("New Category")
                .price(new BigDecimal("149.99"))
                .stock(75)
                .build();

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product savedProduct = productService.saveProduct(productToSave);

        // Assert
        assertNotNull(savedProduct.getCreatedAt());
        assertNotNull(savedProduct.getUpdatedAt());
        verify(productRepository, times(1)).save(productToSave);
    }

    @Test
    void saveProduct_ShouldOnlyUpdateUpdatedAt_WhenCreatedAtIsNotNull() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Product productToUpdate = Product.builder()
                .id(1L)
                .name("Existing Product")
                .description("Existing Description")
                .category("Existing Category")
                .price(new BigDecimal("149.99"))
                .stock(75)
                .createdAt(createdAt)
                .build();

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product updatedProduct = productService.saveProduct(productToUpdate);

        // Assert
        assertEquals(createdAt, updatedProduct.getCreatedAt());
        assertNotNull(updatedProduct.getUpdatedAt());
        verify(productRepository, times(1)).save(productToUpdate);
    }

    @Test
    void findById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void findByName_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.findByName("Test Product");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository, times(1)).findByName("Test Product");
    }

    @Test
    void findAllProducts_ShouldReturnPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findAllProducts(pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(productList, result.getContent());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void findByCategory_ShouldReturnPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findByCategory("Test Category", pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findByCategory("Test Category", pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(productList, result.getContent());
        verify(productRepository, times(1)).findByCategory("Test Category", pageable);
    }


    @Test
    void findByPriceRange_ShouldReturnListOfProducts() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("200.00");
        when(productRepository.findByPriceRange(minPrice, maxPrice)).thenReturn(productList);

        // Act
        List<Product> result = productService.findByPriceRange(minPrice, maxPrice);

        // Assert
        assertEquals(2, result.size());
        assertEquals(productList, result);
        verify(productRepository, times(1)).findByPriceRange(minPrice, maxPrice);
    }

    @Test
    void findLowStockProductsAsync_ShouldReturnCompletableFutureOfProducts() throws ExecutionException, InterruptedException {
        // Arrange
        List<Product> lowStockProducts = List.of(productList.get(1)); // product with stock 50
        when(productRepository.findLowStockProducts(60)).thenReturn(lowStockProducts);

        // Act
        CompletableFuture<List<Product>> futureResult = productService.findLowStockProductsAsync(60);
        List<Product> result = futureResult.get(); // Wait for the future to complete

        // Assert
        assertEquals(1, result.size());
        assertEquals(lowStockProducts, result);
        verify(productRepository, times(1)).findLowStockProducts(60);
    }

    @Test
    void updateStock_ShouldUpdateStockAndReturnProduct_WhenProductExists() {
        // Arrange
        // Create a copy of the test product
        Product productToUpdate = Product.builder()
                .id(testProduct.getId())
                .name(testProduct.getName())
                .description(testProduct.getDescription())
                .category(testProduct.getCategory())
                .price(testProduct.getPrice())
                .stock(testProduct.getStock())
                .createdAt(testProduct.getCreatedAt())
                .updatedAt(testProduct.getUpdatedAt())
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(productToUpdate));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product updatedProduct = productService.updateStock(1L, 200);

        // Assert
        assertEquals(200, updatedProduct.getStock());
        assertNotNull(updatedProduct.getUpdatedAt());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(productToUpdate);
    }

    @Test
    void updateStock_ShouldThrowException_WhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateStock(999L, 200);
        });
        assertEquals("Product not found with ID: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    void deleteProduct_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void countByCategory_ShouldReturnCount() {
        // Arrange
        when(productRepository.countByCategory("Test Category")).thenReturn(2L);

        // Act
        long count = productService.countByCategory("Test Category");

        // Assert
        assertEquals(2L, count);
        verify(productRepository, times(1)).countByCategory("Test Category");
    }

    @Test
    void saveAllProducts_ShouldSetCreatedAtAndUpdatedAt_WhenCreatedAtIsNull() {
        // Arrange
        List<Product> productsToSave = Arrays.asList(
                Product.builder()
                        .name("Bulk Product 1")
                        .description("Bulk Description 1")
                        .category("Bulk Category")
                        .price(new BigDecimal("99.99"))
                        .stock(100)
                        .build(),
                Product.builder()
                        .name("Bulk Product 2")
                        .description("Bulk Description 2")
                        .category("Bulk Category")
                        .price(new BigDecimal("199.99"))
                        .stock(200)
                        .build()
        );

        when(productRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Product> savedProducts = productService.saveAllProducts(productsToSave);

        // Assert
        assertEquals(2, savedProducts.size());
        savedProducts.forEach(product -> {
            assertNotNull(product.getCreatedAt());
            assertNotNull(product.getUpdatedAt());
        });
        verify(productRepository, times(1)).saveAll(productsToSave);
    }
}