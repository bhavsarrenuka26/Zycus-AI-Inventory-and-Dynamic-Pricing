package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void shouldGetAllProducts() throws Exception {
        // Given
        createTestProduct("TEST-001", "Product 1");
        createTestProduct("TEST-002", "Product 2");
        
        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }
    
    @Test
    void shouldCreateProduct() throws Exception {
        // Given
        String productJson = """
            {
                "sku": "NEW-001",
                "name": "New Product",
                "category": "APPAREL",
                "currentPrice": 29.99,
                "stockLevel": 10,
                "reorderThreshold": 5,
                "demandVelocity": 3
            }
            """;
        
        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("NEW-001"))
                .andExpect(jsonPath("$.name").value("New Product"));
    }
    
    private Product createTestProduct(String sku, String name) {
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setCategory(Category.APPAREL);
        product.setCurrentPrice(29.99);
        product.setStockLevel(10);
        product.setReorderThreshold(5);
        product.setDemandVelocity(3);
        product.setStatus(ProductStatus.ACTIVE);
        return productRepository.save(product);
    }
}