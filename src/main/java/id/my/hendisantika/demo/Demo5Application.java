package id.my.hendisantika.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class Demo5Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo5Application.class, args);
    }

}
