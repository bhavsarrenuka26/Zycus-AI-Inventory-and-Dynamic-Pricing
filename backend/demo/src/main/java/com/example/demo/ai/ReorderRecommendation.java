package com.example.demo.ai;

import lombok.Data;

@Data
public class ReorderRecommendation {
    private Integer recommendedQuantity;
    private Integer suggestedLeadTimeDays;
    private Double confidence;
    private String reasoning;
}