package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Category is required")
    private Category category;

    @Positive(message = "Current price must be positive")
    private Double currentPrice;

    @Min(value = 0, message = "Stock level cannot be negative")
    private Integer stockLevel = 0;

    @Min(value = 0, message = "Reorder threshold cannot be negative")
    private Integer reorderThreshold = 0;

    @Min(value = 0, message = "Demand velocity cannot be negative")
    private Integer demandVelocity = 0;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;
}