package com.ecommerce.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class ECommerceApplication {

    private static final Logger logger = LoggerFactory.getLogger(ECommerceApplication.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    public static void main(String[] args) {
        logger.info("=== STARTUP ENV INFO ===");
        logger.info("TEST_VAR env: {}", System.getenv("TEST_VAR"));
        
        // Scan all env keys containing MYSQL, HOST, or PORT
        System.getenv().forEach((k, v) -> {
            if (k.toUpperCase().contains("MYSQL") || k.toUpperCase().contains("HOST") || k.toUpperCase().contains("PORT")) {
                logger.info("Env Key Match: [{}] = [{}]", k, k.toUpperCase().contains("PASSWORD") || k.toUpperCase().contains("SECRET") ? "***" : v);
            }
        });
        
        SpringApplication.run(ECommerceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        logger.info("=== DATASOURCE INFO ===");
        logger.info("Datasource URL: {}", datasourceUrl);
        logger.info("Datasource Username: {}", datasourceUsername);
    }
}
