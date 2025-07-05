package id.my.hendisantika.demo.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that require a PostgreSQL database.
 * Uses TestContainers to start a PostgreSQL container for testing.
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

    /**
     * PostgreSQL container configuration that matches the compose.yaml settings.
     */
    @Container
    protected static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17.5-alpine3.22")
            .withDatabaseName("highperf")
            .withUsername("postgres")
            .withPassword("postgres")
            .withCommand(
                    "postgres " +
                            "-c max_connections=500 " +
                            "-c shared_buffers=1GB " +
                            "-c effective_cache_size=3GB " +
                            "-c work_mem=16MB " +
                            "-c maintenance_work_mem=512MB " +
                            "-c random_page_cost=1.1 " +
                            "-c effective_io_concurrency=200 " +
                            "-c checkpoint_completion_target=0.9 " +
                            "-c wal_buffers=16MB " +
                            "-c default_statistics_target=100 " +
                            "-c autovacuum=on"
            );

    /**
     * Configure Spring Boot to use the TestContainer's PostgreSQL instance.
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        // Ensure Hibernate creates the schema
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        // Disable Spring Boot's Docker Compose support to prevent conflicts with TestContainers
        registry.add("spring.docker.compose.enabled", () -> "false");
    }
}