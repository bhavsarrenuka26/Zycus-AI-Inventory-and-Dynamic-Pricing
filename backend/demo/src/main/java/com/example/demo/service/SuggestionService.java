package com.example.demo.service;

import com.example.demo.ai.AICommerceAdvisor;
import com.example.demo.ai.PricingRecommendation;
import com.example.demo.ai.ReorderRecommendation;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PricingSuggestionRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReorderSuggestionRepository;
import com.example.demo.rules.RuleBasedCommerceAdvisor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SuggestionService {

    private final PricingSuggestionRepository pricingSuggestionRepository;
    private final ReorderSuggestionRepository reorderSuggestionRepository;
    private final ProductRepository productRepository;
    private final AICommerceAdvisor aiCommerceAdvisor;
    private final RuleBasedCommerceAdvisor ruleBasedCommerceAdvisor;
    
    public List<PricingSuggestion> getAllPricingSuggestions() {
        return pricingSuggestionRepository.findAll();
    }

    public List<ReorderSuggestion> getAllReorderSuggestions() {
        return reorderSuggestionRepository.findAll();
    }

    public List<PricingSuggestion> getPendingPricingSuggestions() {
        return pricingSuggestionRepository.findByStatus(SuggestionStatus.PENDING);
    }

    public List<ReorderSuggestion> getPendingReorderSuggestions() {
        return reorderSuggestionRepository.findByStatus(SuggestionStatus.PENDING);
    }
    
    @Transactional
    public PricingSuggestion generatePricingSuggestion(Long productId, TriggerReason triggerReason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if there's already a pending suggestion for this product and trigger
        List<PricingSuggestion> existingSuggestions = pricingSuggestionRepository
                .findByProductIdAndStatusAndTriggerReason(productId, SuggestionStatus.PENDING, triggerReason);

        if (!existingSuggestions.isEmpty()) {
            // Return existing suggestion
            return existingSuggestions.get(0);
        }

        // Generate recommendation using AI advisor with fallback to rule-based
        PricingRecommendation recommendation;
        try {
            recommendation = aiCommerceAdvisor.generatePricingRecommendation(product, triggerReason);
            log.info("Generated AI pricing recommendation for product {}", product.getId());
        } catch (Exception e) {
            log.warn("Failed to generate AI pricing recommendation, falling back to rule-based: {}", e.getMessage());
            recommendation = ruleBasedCommerceAdvisor.generatePricingRecommendation(product, triggerReason);
        }

        // Create and save pricing suggestion
        PricingSuggestion suggestion = new PricingSuggestion();
        suggestion.setProduct(product);
        suggestion.setCurrentPrice(product.getCurrentPrice());
        suggestion.setRecommendedPrice(recommendation.getRecommendedPrice());
        suggestion.setDirection(recommendation.getDirection());
        suggestion.setConfidence(recommendation.getConfidence());
        suggestion.setReasoning(recommendation.getReasoning());
        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setTriggerReason(triggerReason);

        return pricingSuggestionRepository.save(suggestion);
    }
    
    @Transactional
    public ReorderSuggestion generateReorderSuggestion(Long productId, TriggerReason triggerReason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if there's already a pending suggestion for this product and trigger
        List<ReorderSuggestion> existingSuggestions = reorderSuggestionRepository
                .findByProductIdAndStatusAndTriggerReason(productId, SuggestionStatus.PENDING, triggerReason);

        if (!existingSuggestions.isEmpty()) {
            // Return existing suggestion
            return existingSuggestions.get(0);
        }

        // Generate recommendation using AI advisor with fallback to rule-based
        ReorderRecommendation recommendation;
        try {
            recommendation = aiCommerceAdvisor.generateReorderRecommendation(product, triggerReason);
            log.info("Generated AI reorder recommendation for product {}", product.getId());
        } catch (Exception e) {
            log.warn("Failed to generate AI reorder recommendation, falling back to rule-based: {}", e.getMessage());
            recommendation = ruleBasedCommerceAdvisor.generateReorderRecommendation(product, triggerReason);
        }

        // Create and save reorder suggestion
        ReorderSuggestion suggestion = new ReorderSuggestion();
        suggestion.setProduct(product);
        suggestion.setCurrentStock(product.getStockLevel());
        suggestion.setRecommendedQuantity(recommendation.getRecommendedQuantity());
        suggestion.setSuggestedLeadTimeDays(recommendation.getSuggestedLeadTimeDays());
        suggestion.setConfidence(recommendation.getConfidence());
        suggestion.setReasoning(recommendation.getReasoning());
        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setTriggerReason(triggerReason);

        return reorderSuggestionRepository.save(suggestion);
    }
    
    @Transactional
    public PricingSuggestion updatePricingSuggestionStatus(Long suggestionId, SuggestionStatus newStatus) {
        PricingSuggestion suggestion = pricingSuggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing suggestion not found with id: " + suggestionId));

        if (suggestion.getStatus() != SuggestionStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING suggestions can be accepted or rejected");
        }

        suggestion.setStatus(newStatus);
        PricingSuggestion updatedSuggestion = pricingSuggestionRepository.save(suggestion);

        // If accepted, update the product price
        if (newStatus == SuggestionStatus.ACCEPTED) {
            Product product = suggestion.getProduct();
            product.setCurrentPrice(suggestion.getRecommendedPrice());
            productRepository.save(product);
        }

        return updatedSuggestion;
    }

    @Transactional
    public ReorderSuggestion updateReorderSuggestionStatus(Long suggestionId, SuggestionStatus newStatus) {
        ReorderSuggestion suggestion = reorderSuggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Reorder suggestion not found with id: " + suggestionId));

        if (suggestion.getStatus() != SuggestionStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING suggestions can be accepted or rejected");
        }

        suggestion.setStatus(newStatus);
        return reorderSuggestionRepository.save(suggestion);
    }
}