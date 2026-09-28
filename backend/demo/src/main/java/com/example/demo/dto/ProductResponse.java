package com.example.demo.dto;

import com.example.demo.entity.ProductStatus;
import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String category;
    private Double currentPrice;
    private Integer stockLevel;
    private Integer reorderThreshold;
    private Integer demandVelocity;
    private String status;
}