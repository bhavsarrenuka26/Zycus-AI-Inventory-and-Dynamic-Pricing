package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setSku(productDetails.getSku());
        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setCurrentPrice(productDetails.getCurrentPrice());
        product.setStockLevel(productDetails.getStockLevel());
        product.setReorderThreshold(productDetails.getReorderThreshold());
        product.setDemandVelocity(productDetails.getDemandVelocity());
        product.setStatus(productDetails.getStatus());
        
        return productRepository.save(product);
    }
    
    public boolean existsBySku(String sku) {
        return productRepository.findBySku(sku).isPresent();
    }
}