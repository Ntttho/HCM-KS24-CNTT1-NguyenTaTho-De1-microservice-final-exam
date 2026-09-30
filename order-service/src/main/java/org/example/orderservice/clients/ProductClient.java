package org.example.orderservice.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.orderservice.models.dto.responses.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/api/v1/products", fallback = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackFindProduct")
    ProductResponse getProductById(@PathVariable Long id);
}
