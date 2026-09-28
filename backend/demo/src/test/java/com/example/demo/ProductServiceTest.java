package com.example.demo;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void shouldCreateAndGetProduct() {
        // Given
        Product product = new Product();
        product.setSku("TEST-001");
        product.setName("Test Product");
        product.setCategory(Category.APPAREL);
        product.setCurrentPrice(29.99);
        product.setStockLevel(10);
        product.setReorderThreshold(5);
        product.setDemandVelocity(3);
        product.setStatus(ProductStatus.ACTIVE);
        
        // When
        Product savedProduct = productService.createProduct(product);
        Product retrievedProduct = productService.getProductById(savedProduct.getId());
        
        // Then
        assertThat(retrievedProduct).isNotNull();
        assertThat(retrievedProduct.getSku()).isEqualTo("TEST-001");
        assertThat(retrievedProduct.getName()).isEqualTo("Test Product");
        assertThat(retrievedProduct.getCategory()).isEqualTo(Category.APPAREL);
        assertThat(retrievedProduct.getCurrentPrice()).isEqualTo(29.99);
    }
    
    @Test
    void shouldGetAllProducts() {
        // Given
        createTestProduct("TEST-001", "Product 1");
        createTestProduct("TEST-002", "Product 2");
        
        // When
        List<Product> products = productService.getAllProducts();
        
        // Then
        assertThat(products).hasSizeGreaterThanOrEqualTo(2);
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