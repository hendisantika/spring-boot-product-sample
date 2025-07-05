package id.my.hendisantika.demo.util;

import id.my.hendisantika.demo.model.Product;
import id.my.hendisantika.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data loader to initialize the database with sample data.
 * Only runs in "dev" profile to avoid loading test data in production.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private static final String[] CATEGORIES = {
            "Electronics", "Clothing", "Books", "Home", "Sports",
            "Toys", "Beauty", "Grocery", "Automotive", "Garden"
    };
    private static final String[] PRODUCT_NAMES = {
            "Smartphone", "Laptop", "Headphones", "T-shirt", "Jeans",
            "Novel", "Textbook", "Sofa", "Chair", "Basketball",
            "Football", "Doll", "Action Figure", "Shampoo", "Lotion",
            "Bread", "Milk", "Car Parts", "Tools", "Plants"
    };
    private final ProductRepository productRepository;
    private final SecureRandom random = new SecureRandom();

    @Override
    public void run(String... args) {
        log.info("Loading sample data...");

        // Check if data already exists
        if (productRepository.count() > 0) {
            log.info("Data already loaded, skipping initialization");
            return;
        }

        // Create a large batch of products for performance testing
        List<Product> products = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 10000; i++) {
            String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
            String name = PRODUCT_NAMES[random.nextInt(PRODUCT_NAMES.length)] + " " + (i + 1);

            Product product = Product.builder()
                    .name(name)
                    .description("Description for " + name)
                    .category(category)
                    .price(BigDecimal.valueOf(10L + random.nextInt(990)))
                    .stock(random.nextInt(1000))
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            products.add(product);

            // Save in batches of 1000 to avoid memory issues
            if (products.size() % 1000 == 0) {
                productRepository.saveAll(products);
                log.info("Saved batch of {} products", products.size());
                products.clear();
            }
        }

        // Save any remaining products
        if (!products.isEmpty()) {
            productRepository.saveAll(products);
            log.info("Saved final batch of {} products", products.size());
        }

        log.info("Sample data loading complete");
    }
}