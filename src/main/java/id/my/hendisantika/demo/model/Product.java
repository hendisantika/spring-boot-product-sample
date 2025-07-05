package id.my.hendisantika.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity optimized for high-performance operations.
 * Uses indexes on frequently queried fields for faster lookups.
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_category", columnList = "category"),
        @Index(name = "idx_product_price", columnList = "price")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product information")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Schema(description = "Name of the product", example = "Smartphone XYZ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Detailed description of the product", example = "Latest model with advanced features")
    private String description;

    @Schema(description = "Category the product belongs to", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    @Schema(description = "Price of the product", example = "999.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @Schema(description = "Available quantity in stock", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;

    @Schema(description = "Date and time when the product was created", example = "2023-06-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time when the product was last updated", example = "2023-06-16T15:45:00")
    private LocalDateTime updatedAt;
}
