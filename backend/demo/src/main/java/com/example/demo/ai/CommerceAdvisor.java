package com.example.demo.ai;

import com.example.demo.entity.Product;
import com.example.demo.entity.TriggerReason;

public interface CommerceAdvisor {
    PricingRecommendation generatePricingRecommendation(Product product, TriggerReason triggerReason);
    ReorderRecommendation generateReorderRecommendation(Product product, TriggerReason triggerReason);
}