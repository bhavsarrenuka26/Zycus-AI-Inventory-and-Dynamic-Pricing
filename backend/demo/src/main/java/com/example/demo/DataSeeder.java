package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    
    private final ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            log.info("Seeding initial data...");
            
            // Create sample products
            createSampleProducts();
            
            log.info("Data seeding completed.");
        } else {
            log.info("Data already exists, skipping seeding.");
        }
    }
    
    private void createSampleProducts() {
        // Inventory-low demonstration product
        Product product1 = new Product();
        product1.setSku("SKU-APP-001");
        product1.setName("Organic Cotton T-Shirt");
        product1.setCategory(Category.APPAREL);
        product1.setCurrentPrice(24.99);
        product1.setStockLevel(8);
        product1.setReorderThreshold(15);
        product1.setDemandVelocity(12);
        product1.setStatus(ProductStatus.PRICE_REVIEW_PENDING);
        productRepository.save(product1);
        
        // Demand-spike demonstration product
        Product product2 = new Product();
        product2.setSku("SKU-APP-003");
        product2.setName("Hoodie — Heather Grey");
        product2.setCategory(Category.APPAREL);
        product2.setCurrentPrice(54.99);
        product2.setStockLevel(11);
        product2.setReorderThreshold(12);
        product2.setDemandVelocity(15);
        product2.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product2);
        
        // Additional products for category averages
        Product product3 = new Product();
        product3.setSku("SKU-APP-002");
        product3.setName("Denim Jeans");
        product3.setCategory(Category.APPAREL);
        product3.setCurrentPrice(79.99);
        product3.setStockLevel(25);
        product3.setReorderThreshold(20);
        product3.setDemandVelocity(8);
        product3.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product3);
        
        Product product4 = new Product();
        product4.setSku("SKU-ELE-001");
        product4.setName("Wireless Headphones");
        product4.setCategory(Category.ELECTRONICS);
        product4.setCurrentPrice(129.99);
        product4.setStockLevel(15);
        product4.setReorderThreshold(10);
        product4.setDemandVelocity(22);
        product4.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product4);
        
        Product product5 = new Product();
        product5.setSku("SKU-ELE-002");
        product5.setName("Smartphone Charger");
        product5.setCategory(Category.ELECTRONICS);
        product5.setCurrentPrice(19.99);
        product5.setStockLevel(50);
        product5.setReorderThreshold(30);
        product5.setDemandVelocity(35);
        product5.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product5);
        
        Product product6 = new Product();
        product6.setSku("SKU-HOM-001");
        product6.setName("Ceramic Coffee Mug Set");
        product6.setCategory(Category.HOME);
        product6.setCurrentPrice(34.99);
        product6.setStockLevel(18);
        product6.setReorderThreshold(12);
        product6.setDemandVelocity(5);
        product6.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product6);
        
        Product product7 = new Product();
        product7.setSku("SKU-HOM-002");
        product7.setName("Throw Pillow");
        product7.setCategory(Category.HOME);
        product7.setCurrentPrice(22.99);
        product7.setStockLevel(32);
        product7.setReorderThreshold(25);
        product7.setDemandVelocity(3);
        product7.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product7);
        
        log.info("Created {} sample products", 7);
    }
}