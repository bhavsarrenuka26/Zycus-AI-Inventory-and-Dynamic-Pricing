package com.example.demo.rules;

import com.example.demo.ai.CommerceAdvisor;
import com.example.demo.ai.PricingRecommendation;
import com.example.demo.ai.ReorderRecommendation;
import com.example.demo.entity.Product;
import com.example.demo.entity.SuggestionDirection;
import com.example.demo.entity.TriggerReason;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RuleBasedCommerceAdvisor implements CommerceAdvisor {
    
    private final ProductRepository productRepository;
    
    @Override
    public PricingRecommendation generatePricingRecommendation(Product product, TriggerReason triggerReason) {
        PricingRecommendation recommendation = new PricingRecommendation();
        
        // Calculate category average demand velocity
        Double categoryAverage = productRepository.getAverageDemandVelocityByCategory(product.getCategory());
        if (categoryAverage == null) {
            categoryAverage = 0.0;
        }
        
        double recommendedPrice;
        SuggestionDirection direction;
        
        if (product.getStockLevel() < product.getReorderThreshold()) {
            // Inventory low - increase price
            recommendedPrice = product.getCurrentPrice() * 1.10;
            direction = SuggestionDirection.INCREASE;
        } else if (product.getDemandVelocity() > 2 * categoryAverage) {
            // High demand - increase price slightly
            recommendedPrice = product.getCurrentPrice() * 1.05;
            direction = SuggestionDirection.INCREASE;
        } else {
            // Hold current price
            recommendedPrice = product.getCurrentPrice();
            direction = SuggestionDirection.HOLD;
        }
        
        recommendation.setRecommendedPrice(recommendedPrice);
        recommendation.setDirection(direction);
        recommendation.setConfidence(0.9); // High confidence for rule-based recommendations
        recommendation.setReasoning(generatePricingReasoning(product, direction, triggerReason, categoryAverage));
        
        return recommendation;
    }
    
    @Override
    public ReorderRecommendation generateReorderRecommendation(Product product, TriggerReason triggerReason) {
        ReorderRecommendation recommendation = new ReorderRecommendation();
        
        // Calculate recommended quantity: (reorderThreshold * 3) - currentStock
        int recommendedQuantity = Math.max(1, (product.getReorderThreshold() * 3) - product.getStockLevel());
        
        recommendation.setRecommendedQuantity(recommendedQuantity);
        recommendation.setSuggestedLeadTimeDays(5); // Default lead time
        recommendation.setConfidence(0.85); // Medium-high confidence for rule-based recommendations
        recommendation.setReasoning(generateReorderReasoning(product, triggerReason));
        
        return recommendation;
    }
    
    private String generatePricingReasoning(Product product, SuggestionDirection direction, TriggerReason triggerReason, Double categoryAverage) {
        StringBuilder reasoning = new StringBuilder();
        
        switch (triggerReason) {
            case INVENTORY_LOW:
                reasoning.append("Inventory level (")
                        .append(product.getStockLevel())
                        .append(") is below reorder threshold (")
                        .append(product.getReorderThreshold())
                        .append("). ");
                break;
            case DEMAND_SPIKE:
                reasoning.append("Demand velocity (")
                        .append(product.getDemandVelocity())
                        .append(") exceeds twice the category average (")
                        .append(String.format("%.2f", categoryAverage))
                        .append("). ");
                break;
            case INITIAL:
                reasoning.append("Initial pricing analysis. ");
                break;
            case MANUAL:
                reasoning.append("Manual pricing analysis requested. ");
                break;
        }
        
        switch (direction) {
            case INCREASE:
                reasoning.append("Recommend price increase based on market conditions.");
                break;
            case DECREASE:
                reasoning.append("Recommend price decrease based on market conditions.");
                break;
            case HOLD:
                reasoning.append("Recommend holding current price.");
                break;
        }
        
        return reasoning.toString();
    }
    
    private String generateReorderReasoning(Product product, TriggerReason triggerReason) {
        StringBuilder reasoning = new StringBuilder();
        
        reasoning.append("Current stock level is ")
                .append(product.getStockLevel())
                .append(", reorder threshold is ")
                .append(product.getReorderThreshold())
                .append(". ");
        
        switch (triggerReason) {
            case INVENTORY_LOW:
                reasoning.append("Inventory level is below threshold. ");
                break;
            case DEMAND_SPIKE:
                reasoning.append("High demand detected. ");
                break;
            case INITIAL:
                reasoning.append("Initial reorder analysis. ");
                break;
            case MANUAL:
                reasoning.append("Manual reorder analysis requested. ");
                break;
        }
        
        reasoning.append("Recommended quantity calculated as (threshold × 3) - current stock.");
        
        return reasoning.toString();
    }
}