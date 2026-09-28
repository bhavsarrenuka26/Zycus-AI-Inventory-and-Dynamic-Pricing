package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import com.example.demo.entity.TriggerReason;
import com.example.demo.event.InventoryChangedEvent;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {
    
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public Product adjustStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        int newStockLevel = product.getStockLevel() + quantity;
        if (newStockLevel < 0) {
            throw new IllegalArgumentException("Cannot reduce stock below zero");
        }
        
        product.setStockLevel(newStockLevel);
        
        // Update product status based on stock level
        if (newStockLevel == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }
        
        Product updatedProduct = productRepository.save(product);
        
        // Determine trigger reason
        TriggerReason triggerReason = determineTriggerReason(updatedProduct, quantity);
        
        // Publish inventory changed event
        eventPublisher.publishEvent(new InventoryChangedEvent(this, updatedProduct, triggerReason, quantity));
        
        return updatedProduct;
    }
    
    private TriggerReason determineTriggerReason(Product product, int quantityChange) {
        if (quantityChange < 0) {
            // Stock reduction (sale)
            if (product.getStockLevel() < product.getReorderThreshold()) {
                return TriggerReason.INVENTORY_LOW;
            }
            
            // Check for demand spike (simplified check)
            if (product.getDemandVelocity() > 10) { // Arbitrary threshold for demo
                return TriggerReason.DEMAND_SPIKE;
            }
        }
        
        return TriggerReason.MANUAL;
    }
}