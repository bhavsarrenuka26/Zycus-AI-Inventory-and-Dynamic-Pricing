package com.example.demo.ai;

import com.example.demo.entity.Product;
import com.example.demo.entity.SuggestionDirection;
import com.example.demo.entity.TriggerReason;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AICommerceAdvisor implements CommerceAdvisor {
    
    private final LLMGateway llmGateway;
    private final ObjectMapper objectMapper;
    
    @Override
    public PricingRecommendation generatePricingRecommendation(Product product, TriggerReason triggerReason) {
        try {
            // Calculate category average demand velocity
            Double categoryAverage = 0.0; // This would typically come from a service
            
            String prompt = """
                Based on the following product information, provide a pricing recommendation in JSON format:
                
                Product Name: %s
                Category: %s
                Current Price: $%.2f
                Current Stock: %d
                Reorder Threshold: %d
                Demand Velocity: %d
                Category Average Demand: %.2f
                Trigger Reason: %s
                
                Please respond with ONLY valid JSON in this exact format:
                {
                  "recommendedPrice": 29.99,
                  "direction": "INCREASE",
                  "confidence": 0.82,
                  "reasoning": "..."
                }
                
                Rules:
                - Return valid JSON only
                - No markdown or formatting
                - recommendedPrice must be positive
                - confidence must be between 0 and 1
                - direction must be INCREASE, DECREASE, or HOLD
                - reasoning must be concise
                """.formatted(
                    product.getName(),
                    product.getCategory(),
                    product.getCurrentPrice(),
                    product.getStockLevel(),
                    product.getReorderThreshold(),
                    product.getDemandVelocity(),
                    categoryAverage,
                    triggerReason
                );
            
            String response = llmGateway.chatCompletion(prompt);
            
            // Extract the JSON from the response
            String jsonContent = extractJsonFromResponse(response);
            
            // Parse the JSON response
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            
            PricingRecommendation recommendation = new PricingRecommendation();
            recommendation.setRecommendedPrice(jsonNode.get("recommendedPrice").asDouble());
            recommendation.setDirection(SuggestionDirection.valueOf(jsonNode.get("direction").asText()));
            recommendation.setConfidence(jsonNode.get("confidence").asDouble());
            recommendation.setReasoning(jsonNode.get("reasoning").asText());
            
            // Validate the recommendation
            validatePricingRecommendation(recommendation);
            
            return recommendation;
        } catch (Exception e) {
            log.error("Error generating AI pricing recommendation: {}", e.getMessage());
            throw new RuntimeException("Failed to generate AI pricing recommendation", e);
        }
    }
    
    @Override
    public ReorderRecommendation generateReorderRecommendation(Product product, TriggerReason triggerReason) {
        try {
            // Calculate category average demand velocity
            Double categoryAverage = 0.0; // This would typically come from a service
            
            String prompt = """
                Based on the following product information, provide a reorder recommendation in JSON format:
                
                Product Name: %s
                Category: %s
                Current Price: $%.2f
                Current Stock: %d
                Reorder Threshold: %d
                Demand Velocity: %d
                Category Average Demand: %.2f
                Trigger Reason: %s
                
                Please respond with ONLY valid JSON in this exact format:
                {
                  "recommendedQuantity": 37,
                  "suggestedLeadTimeDays": 5,
                  "confidence": 0.78,
                  "reasoning": "..."
                }
                
                Rules:
                - Return valid JSON only
                - No markdown or formatting
                - recommendedQuantity must be at least 1
                - confidence must be between 0 and 1
                - suggestedLeadTimeDays must be 0 or positive
                - reasoning must be concise
                """.formatted(
                    product.getName(),
                    product.getCategory(),
                    product.getCurrentPrice(),
                    product.getStockLevel(),
                    product.getReorderThreshold(),
                    product.getDemandVelocity(),
                    categoryAverage,
                    triggerReason
                );
            
            String response = llmGateway.chatCompletion(prompt);
            
            // Extract the JSON from the response
            String jsonContent = extractJsonFromResponse(response);
            
            // Parse the JSON response
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            
            ReorderRecommendation recommendation = new ReorderRecommendation();
            recommendation.setRecommendedQuantity(jsonNode.get("recommendedQuantity").asInt());
            recommendation.setSuggestedLeadTimeDays(jsonNode.get("suggestedLeadTimeDays").asInt());
            recommendation.setConfidence(jsonNode.get("confidence").asDouble());
            recommendation.setReasoning(jsonNode.get("reasoning").asText());
            
            // Validate the recommendation
            validateReorderRecommendation(recommendation);
            
            return recommendation;
        } catch (Exception e) {
            log.error("Error generating AI reorder recommendation: {}", e.getMessage());
            throw new RuntimeException("Failed to generate AI reorder recommendation", e);
        }
    }
    
    private String extractJsonFromResponse(String response) {
        // Simple extraction - in a real implementation, you might need more sophisticated parsing
        return response.trim();
    }
    
    private void validatePricingRecommendation(PricingRecommendation recommendation) {
        if (recommendation.getRecommendedPrice() <= 0) {
            throw new IllegalArgumentException("Recommended price must be positive");
        }
        
        if (recommendation.getConfidence() == null || recommendation.getConfidence() < 0 || recommendation.getConfidence() > 1) {
            throw new IllegalArgumentException("Confidence must be between 0 and 1");
        }
        
        if (recommendation.getDirection() == null) {
            throw new IllegalArgumentException("Direction is required");
        }
        
        if (recommendation.getReasoning() == null || recommendation.getReasoning().isEmpty()) {
            throw new IllegalArgumentException("Reasoning is required");
        }
    }
    
    private void validateReorderRecommendation(ReorderRecommendation recommendation) {
        if (recommendation.getRecommendedQuantity() == null || recommendation.getRecommendedQuantity() < 1) {
            throw new IllegalArgumentException("Recommended quantity must be at least 1");
        }
        
        if (recommendation.getSuggestedLeadTimeDays() == null || recommendation.getSuggestedLeadTimeDays() < 0) {
            throw new IllegalArgumentException("Suggested lead time must be 0 or positive");
        }
        
        if (recommendation.getConfidence() == null || recommendation.getConfidence() < 0 || recommendation.getConfidence() > 1) {
            throw new IllegalArgumentException("Confidence must be between 0 and 1");
        }
        
        if (recommendation.getReasoning() == null || recommendation.getReasoning().isEmpty()) {
            throw new IllegalArgumentException("Reasoning is required");
        }
    }
}