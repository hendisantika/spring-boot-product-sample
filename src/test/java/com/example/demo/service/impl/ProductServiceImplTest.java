package com.example.demo.service.impl;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
}
