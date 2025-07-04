package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for caching using Caffeine.
 * Optimized for high performance with appropriate cache sizes and expiration times.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager configured with Caffeine for high performance.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
                "products",
                "productsByName",
                "allProducts",
                "productsByCategory",
                "productsByPriceRange",
                "productCountByCategory"
        ));
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Caffeine cache builder with optimized settings.
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                // Initial capacity of the cache
                .initialCapacity(100)
                // Maximum size of the cache
                .maximumSize(10000)
                // Expire entries after write
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // Expire entries after access
                .expireAfterAccess(10, TimeUnit.MINUTES)
                // Record statistics for monitoring
                .recordStats();
    }
}