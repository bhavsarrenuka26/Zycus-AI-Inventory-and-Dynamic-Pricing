package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pricing_suggestions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Double currentPrice;

    private Double recommendedPrice;

    @Enumerated(EnumType.STRING)
    private SuggestionDirection direction;

    private Double confidence;

    @Column(length = 1000)
    private String reasoning;

    @Enumerated(EnumType.STRING)
    private SuggestionStatus status = SuggestionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TriggerReason triggerReason;
}