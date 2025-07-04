# High-Performance Spring Boot Application

A Spring Boot application designed to handle 10,000 requests per second with PostgreSQL as the database.

## Features

- Optimized for high throughput with 10K+ requests/second capability
- PostgreSQL database with optimized configuration
- Connection pooling with HikariCP
- Caching with Caffeine
- Asynchronous processing with Java 21 virtual threads
- RESTful API with pagination support
- Interactive API documentation with Swagger UI
- Docker Compose setup for easy deployment

## Technologies

- Java 21
- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL 16
- HikariCP
- Caffeine Cache
- SpringDoc OpenAPI (Swagger)
- Docker & Docker Compose

## Performance Optimizations

1. **Database Optimizations**
    - Optimized PostgreSQL configuration
    - Connection pooling with HikariCP
    - Batch processing for bulk operations
    - Indexed database tables

2. **Application Optimizations**
    - Caching with Caffeine
    - Asynchronous processing with virtual threads
    - Pagination for large result sets
    - Optimized JPA/Hibernate settings
    - Java 21 virtual threads for non-blocking I/O operations

3. **Server Optimizations**
    - Tomcat configured to use virtual threads for request handling
    - Virtual thread per task executor for async operations
    - Optimized for high concurrency with minimal resource usage

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose
- Maven

### Running the Application

1. Clone the repository
2. Start the PostgreSQL database:
   ```
   docker-compose up -d
   ```
3. Run the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```

The application will be available at http://localhost:8080

## API Endpoints

### Products API

- `GET /api/products` - Get all products (paginated)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/name/{name}` - Get product by name
- `GET /api/products/category/{category}` - Get products by category (paginated)
- `GET /api/products/price-range?min={min}&max={max}` - Get products by price range
- `GET /api/products/low-stock/{threshold}` - Get products with stock below threshold (async)
- `GET /api/products/count/category/{category}` - Count products by category
- `POST /api/products` - Create a new product
- `POST /api/products/bulk` - Bulk create products
- `PATCH /api/products/{id}/stock/{stock}` - Update product stock
- `DELETE /api/products/{id}` - Delete a product

### Curl Examples

```bash
# Get all products (paginated)
curl -X GET "http://localhost:8080/api/products?page=0&size=20&sort=id"

# Get product by ID
curl -X GET "http://localhost:8080/api/products/1"

# Get product by name
curl -X GET "http://localhost:8080/api/products/name/Smartphone%201"

# Get products by category (paginated)
curl -X GET "http://localhost:8080/api/products/category/Electronics?page=0&size=20"

# Get products by price range
curl -X GET "http://localhost:8080/api/products/price-range?min=100&max=500"

# Get products with stock below threshold (async)
curl -X GET "http://localhost:8080/api/products/low-stock/10"

# Count products by category
curl -X GET "http://localhost:8080/api/products/count/category/Electronics"

# Create a new product
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Smartphone",
    "description": "Latest model",
    "category": "Electronics",
    "price": 999.99,
    "stock": 100
  }'

# Bulk create products
curl -X POST "http://localhost:8080/api/products/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "name": "Product 1",
      "description": "Description 1",
      "category": "Category 1",
      "price": 99.99,
      "stock": 50
    },
    {
      "name": "Product 2",
      "description": "Description 2",
      "category": "Category 2",
      "price": 199.99,
      "stock": 100
    }
  ]'

# Update product stock
curl -X PATCH "http://localhost:8080/api/products/1/stock/200"

# Delete a product
curl -X DELETE "http://localhost:8080/api/products/1"
```

## Performance Testing

To test the application's performance and achieve 10K requests/second:

1. Load test data (automatically loaded in dev profile)
2. Use the tools below to simulate high load
3. Monitor performance using Spring Boot Actuator endpoints:
    - `http://localhost:8080/actuator/health`
    - `http://localhost:8080/actuator/metrics`
    - `http://localhost:8080/actuator/prometheus`

### k6 Load Testing

[k6](https://k6.io/) is a modern load testing tool that makes it easy to test the performance of your APIs.

1. Install k6:
   ```bash
   # macOS
   brew install k6

   # Linux
   sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
   echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
   sudo apt-get update
   sudo apt-get install k6

   # Windows
   choco install k6
   ```

2. Create a k6 script file named `load-test.js`:
   ```javascript
   import http from 'k6/http';
   import { check, sleep } from 'k6';

   export const options = {
     stages: [
       { duration: '30s', target: 100 },   // Ramp-up to 100 users
       { duration: '1m', target: 1000 },   // Ramp-up to 1000 users
       { duration: '3m', target: 5000 },   // Ramp-up to 5000 users
       { duration: '5m', target: 10000 },  // Ramp-up to 10000 users
       { duration: '10m', target: 10000 }, // Stay at 10000 users for 10 minutes
       { duration: '1m', target: 0 },      // Ramp-down to 0 users
     ],
     thresholds: {
       http_req_duration: ['p(95)<500'], // 95% of requests should be below 500ms
       http_req_failed: ['rate<0.01'],   // Less than 1% of requests should fail
     },
   };

   export default function () {
     const BASE_URL = 'http://localhost:8080/api';

     // Get all products (paginated)
     const productsResponse = http.get(`${BASE_URL}/products?page=0&size=20&sort=id`);
     check(productsResponse, {
       'status is 200': (r) => r.status === 200,
     });

     // Get products by category
     const categoryResponse = http.get(`${BASE_URL}/products/category/Electronics?page=0&size=10`);
     check(categoryResponse, {
       'status is 200': (r) => r.status === 200,
     });

     // Get products by price range
     const priceResponse = http.get(`${BASE_URL}/products/price-range?min=100&max=500`);
     check(priceResponse, {
       'status is 200': (r) => r.status === 200,
     });

     sleep(1);
   }
   ```

3. Run the k6 test:
   ```bash
   k6 run load-test.js
   ```

### Apache Benchmark (ab) Testing

Apache Benchmark is a simple command-line tool for benchmarking HTTP servers.

1. Install Apache Benchmark:
   ```bash
   # macOS
   brew install apache-utils

   # Ubuntu/Debian
   sudo apt-get install apache2-utils

   # CentOS/RHEL
   sudo yum install httpd-tools
   ```

2. Run Apache Benchmark tests:
   ```bash
   # Test GET /api/products endpoint with 10,000 requests and 1,000 concurrent users
   ab -n 10000 -c 1000 http://localhost:8080/api/products?page=0&size=20

   # Test GET /api/products/category/Electronics endpoint
   ab -n 10000 -c 1000 http://localhost:8080/api/products/category/Electronics?page=0&size=20

   # Test POST endpoint with a JSON payload (save to post_data.json first)
   echo '{"name":"Test Product","description":"Test Description","category":"Test","price":99.99,"stock":100}' > post_data.json
   ab -n 10000 -c 1000 -p post_data.json -T application/json http://localhost:8080/api/products
   ```

### JMeter Testing

Apache JMeter is a powerful tool for load testing and performance measurement.

1. Download and install [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi)

2. Create a test plan:
    - Launch JMeter
    - Right-click on "Test Plan" > Add > Threads > Thread Group
    - Set Number of Threads (users) to ramp up to 10,000
    - Set Ramp-up period to 300 seconds (5 minutes)
    - Set Loop Count to 10

3. Add HTTP Request samplers:
    - Right-click on Thread Group > Add > Sampler > HTTP Request
    - Set Server Name: localhost
    - Set Port Number: 8080
    - Set Method: GET
    - Set Path: /api/products
    - Add Parameters: page=0, size=20

4. Add more HTTP Request samplers for other endpoints

5. Add listeners to view results:
    - Right-click on Thread Group > Add > Listener > View Results Tree
    - Right-click on Thread Group > Add > Listener > Aggregate Report

6. Save the test plan and run it

7. Command-line execution for CI/CD:
   ```bash
   jmeter -n -t test_plan.jmx -l results.jtl
   ```

### Achieving 10K Requests/Second

To achieve 10K requests/second, consider the following tips:

1. Run load tests from multiple machines to distribute the client load
2. Ensure your test environment has sufficient resources:
    - At least 16GB RAM for the test client machines
    - Fast network connection between test clients and server
    - Monitoring of client resources to ensure they're not the bottleneck

3. Optimize your test scripts:
    - Use connection pooling in your test clients
    - Minimize sleep times between requests
    - Consider using async requests in k6

4. Monitor server resources during testing:
    - CPU usage
    - Memory usage
    - Network I/O
    - Database connection pool
    - Thread pool utilization

## Configuration

Key configuration properties in `application.properties`:

- Server settings: `server.tomcat.*` (including `server.tomcat.threads.type=virtual` for virtual threads)
- Database connection: `spring.datasource.*`
- Connection pool: `spring.datasource.hikari.*`
- JPA/Hibernate: `spring.jpa.*`
- Caching: `spring.cache.*`
- API Documentation: `springdoc.*` (paths and UI configuration for Swagger)

The application uses Java 21 virtual threads in two key areas:

1. For handling HTTP requests via Tomcat's thread pool (configured with `server.tomcat.threads.type=virtual`)
2. For asynchronous task execution via a custom executor that creates a new virtual thread per task

## Docker Compose

The `compose.yaml` file includes a PostgreSQL service with optimized settings for high performance.

## API Documentation

The application includes comprehensive API documentation using SpringDoc OpenAPI (Swagger).

### Accessing the Documentation

- **Swagger UI**: Browse the interactive API documentation
  at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: Access the OpenAPI specification at [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Features

- Interactive documentation with Swagger UI
- Try-out functionality to test API endpoints directly from the browser
- Detailed information about request parameters, response models, and status codes
- API grouping by tags
- Search functionality to quickly find endpoints

The API documentation is automatically generated based on the code and enhanced with annotations for better clarity and
usability.
