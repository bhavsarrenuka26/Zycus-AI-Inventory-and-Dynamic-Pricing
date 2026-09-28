package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.SuggestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SuggestionServiceTest {

    @Autowired
    private SuggestionService suggestionService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void shouldGeneratePricingSuggestion() {
        // Given
        Product product = createTestProduct();
        
        // When
        PricingSuggestion suggestion = suggestionService.generatePricingSuggestion(
            product.getId(), TriggerReason.MANUAL);
        
        // Then
        assertThat(suggestion).isNotNull();
        assertThat(suggestion.getProduct().getId()).isEqualTo(product.getId());
        assertThat(suggestion.getStatus()).isEqualTo(SuggestionStatus.PENDING);
        assertThat(suggestion.getRecommendedPrice()).isNotNull();
        assertThat(suggestion.getDirection()).isNotNull();
        assertThat(suggestion.getConfidence()).isNotNull();
        assertThat(suggestion.getReasoning()).isNotEmpty();
    }
    
    @Test
    void shouldGenerateReorderSuggestion() {
        // Given
        Product product = createTestProduct();
        
        // When
        ReorderSuggestion suggestion = suggestionService.generateReorderSuggestion(
            product.getId(), TriggerReason.MANUAL);
        
        // Then
        assertThat(suggestion).isNotNull();
        assertThat(suggestion.getProduct().getId()).isEqualTo(product.getId());
        assertThat(suggestion.getStatus()).isEqualTo(SuggestionStatus.PENDING);
        assertThat(suggestion.getRecommendedQuantity()).isNotNull();
        assertThat(suggestion.getSuggestedLeadTimeDays()).isNotNull();
        assertThat(suggestion.getConfidence()).isNotNull();
        assertThat(suggestion.getReasoning()).isNotEmpty();
    }
    
    @Test
    void shouldUpdatePricingSuggestionStatus() {
        // Given
        Product product = createTestProduct();
        PricingSuggestion suggestion = suggestionService.generatePricingSuggestion(
            product.getId(), TriggerReason.MANUAL);
        
        // When
        PricingSuggestion updatedSuggestion = suggestionService.updatePricingSuggestionStatus(
            suggestion.getId(), SuggestionStatus.ACCEPTED);
        
        // Then
        assertThat(updatedSuggestion.getStatus()).isEqualTo(SuggestionStatus.ACCEPTED);
    }
    
    @Test
    void shouldUpdateReorderSuggestionStatus() {
        // Given
        Product product = createTestProduct();
        ReorderSuggestion suggestion = suggestionService.generateReorderSuggestion(
            product.getId(), TriggerReason.MANUAL);
        
        // When
        ReorderSuggestion updatedSuggestion = suggestionService.updateReorderSuggestionStatus(
            suggestion.getId(), SuggestionStatus.REJECTED);
        
        // Then
        assertThat(updatedSuggestion.getStatus()).isEqualTo(SuggestionStatus.REJECTED);
    }
    
    private Product createTestProduct() {
        Product product = new Product();
        product.setSku("SUG-TEST-001");
        product.setName("Suggestion Test Product");
        product.setCategory(Category.APPAREL);
        product.setCurrentPrice(29.99);
        product.setStockLevel(10);
        product.setReorderThreshold(5);
        product.setDemandVelocity(3);
        product.setStatus(ProductStatus.ACTIVE);
        return productRepository.save(product);
    }
}