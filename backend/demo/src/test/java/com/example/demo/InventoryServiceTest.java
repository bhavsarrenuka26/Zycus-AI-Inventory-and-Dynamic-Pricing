package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void shouldAdjustStock() {
        // Given
        Product product = createTestProduct();
        Long productId = product.getId();
        
        // When
        Product updatedProduct = inventoryService.adjustStock(productId, 5);
        
        // Then
        assertThat(updatedProduct.getStockLevel()).isEqualTo(15); // 10 + 5
    }
    
    @Test
    void shouldReduceStock() {
        // Given
        Product product = createTestProduct();
        Long productId = product.getId();
        
        // When
        Product updatedProduct = inventoryService.adjustStock(productId, -3);
        
        // Then
        assertThat(updatedProduct.getStockLevel()).isEqualTo(7); // 10 - 3
    }
    
    @Test
    void shouldNotAllowNegativeStock() {
        // Given
        Product product = createTestProduct();
        Long productId = product.getId();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.adjustStock(productId, -15); // Would result in negative stock
        });
    }
    
    @Test
    void shouldUpdateStatusToOutOfStock() {
        // Given
        Product product = createTestProduct();
        Long productId = product.getId();
        
        // When
        Product updatedProduct = inventoryService.adjustStock(productId, -10); // Reduce to 0
        
        // Then
        assertThat(updatedProduct.getStockLevel()).isEqualTo(0);
        assertThat(updatedProduct.getStatus()).isEqualTo(com.example.demo.entity.ProductStatus.OUT_OF_STOCK);
    }
    
    private Product createTestProduct() {
        Product product = new Product();
        product.setSku("INV-TEST-001");
        product.setName("Inventory Test Product");
        product.setCategory(Category.APPAREL);
        product.setCurrentPrice(29.99);
        product.setStockLevel(10);
        product.setReorderThreshold(5);
        product.setDemandVelocity(3);
        product.setStatus(ProductStatus.ACTIVE);
        return productRepository.save(product);
    }
}