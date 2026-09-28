package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.InventoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final SuggestionService suggestionService;
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product);
    }
    
    @PatchMapping("/{id}/stock")
    public Product adjustStock(@PathVariable Long id, @RequestParam int quantity) {
        return inventoryService.adjustStock(id, quantity);
    }
    
    @PostMapping("/{id}/orders")
    public Product createOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest) {
        return inventoryService.adjustStock(id, -orderRequest.getQuantity());
    }
    
    @PostMapping("/{id}/suggest-pricing")
    public ResponseEntity<?> suggestPricing(@PathVariable Long id) {
        try {
            suggestionService.generatePricingSuggestion(id, com.example.demo.entity.TriggerReason.MANUAL);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/suggest-reorder")
    public ResponseEntity<?> suggestReorder(@PathVariable Long id) {
        try {
            suggestionService.generateReorderSuggestion(id, com.example.demo.entity.TriggerReason.MANUAL);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}