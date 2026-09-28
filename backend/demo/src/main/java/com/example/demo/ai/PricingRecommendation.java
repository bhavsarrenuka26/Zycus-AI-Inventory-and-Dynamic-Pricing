package com.example.demo.ai;

import com.example.demo.entity.SuggestionDirection;
import lombok.Data;

@Data
public class PricingRecommendation {
    private Double recommendedPrice;
    private SuggestionDirection direction;
    private Double confidence;
    private String reasoning;
}