package id.my.hendisantika.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.demo.config.AbstractIntegrationTest;
import id.my.hendisantika.demo.model.Product;
import id.my.hendisantika.demo.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test
        productRepository.deleteAll();

        // Setup test data
        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .category("Test Category")
                .price(new BigDecimal("99.99"))
                .stock(100)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product product2 = Product.builder()
                .name("Test Product 2")
                .description("Test Description 2")
                .category("Test Category")
                .price(new BigDecimal("199.99"))
                .stock(50)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productList = Arrays.asList(testProduct, product2);
        productRepository.saveAll(productList);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        Product newProduct = Product.builder()
                .name("New Product")
                .description("New Description")
                .category("New Category")
                .price(new BigDecimal("149.99"))
                .stock(75)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.category", is("New Category")))
                .andExpect(jsonPath("$.price", is(149.99)))
                .andExpect(jsonPath("$.stock", is(75)))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        // Get the ID of the saved product
        Long productId = productRepository.findByName("Test Product").orElseThrow().getId();

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.category", is("Test Category")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.stock", is(100)));
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductByName_ShouldReturnProduct_WhenProductExists() throws Exception {
        mockMvc.perform(get("/api/products/name/{name}", "Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.category", is("Test Category")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.stock", is(100)));
    }

    @Test
    void getProductByName_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/name/{name}", "Nonexistent Product"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Test Product")))
                .andExpect(jsonPath("$.content[1].name", is("Test Product 2")));
    }

    @Test
    void getProductsByCategory_ShouldReturnPageOfProducts() throws Exception {
        mockMvc.perform(get("/api/products/category/{category}", "Test Category")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].category", is("Test Category")))
                .andExpect(jsonPath("$.content[1].category", is("Test Category")));
    }

    @Test
    void getProductsByPriceRange_ShouldReturnListOfProducts() throws Exception {
        mockMvc.perform(get("/api/products/price-range")
                        .param("min", "50.00")
                        .param("max", "200.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(99.99)))
                .andExpect(jsonPath("$[1].price", is(199.99)));
    }

    @Test
    void getLowStockProducts_ShouldReturnListOfProducts() throws Exception {
        // For async endpoints, we need to use MvcResult to get the actual response
        MvcResult mvcResult = mockMvc.perform(get("/api/products/low-stock/{threshold}", 60))
                .andExpect(status().isOk())
                .andReturn();

        // Wait for the async result to complete
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product 2")))
                .andExpect(jsonPath("$[0].stock", is(50)));
    }


    @Test
    void updateProductStock_ShouldUpdateStockAndReturnProduct() throws Exception {
        // Get the ID of the saved product
        Long productId = productRepository.findByName("Test Product").orElseThrow().getId();

        mockMvc.perform(patch("/api/products/{id}/stock/{stock}", productId, 200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.stock", is(200)));
    }

    @Test
    void updateProductStock_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(patch("/api/products/999/stock/200"))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteProduct_ShouldDeleteProduct() throws Exception {
        // Get the ID of the saved product
        Long productId = productRepository.findByName("Test Product").orElseThrow().getId();

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        // Verify the product is deleted
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void countProductsByCategory_ShouldReturnCount() throws Exception {
        mockMvc.perform(get("/api/products/count/category/{category}", "Test Category"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void createProductsBulk_ShouldCreateMultipleProducts() throws Exception {
        List<Product> newProducts = Arrays.asList(
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

        mockMvc.perform(post("/api/products/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProducts)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Bulk Product 1")))
                .andExpect(jsonPath("$[1].name", is("Bulk Product 2")));
    }
}
