package id.my.hendisantika.demo;

import id.my.hendisantika.demo.config.AbstractIntegrationTest;
import id.my.hendisantika.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class Demo5ApplicationTests extends AbstractIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testDatabaseConnection() {
        // Test that we can get a connection from the DataSource
        assertThat(dataSource).isNotNull();

        // Test that we can execute a simple query
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);

        // Test that the product table exists by counting records
        Long count = productRepository.count();
        assertThat(count).isNotNull();
    }
}
