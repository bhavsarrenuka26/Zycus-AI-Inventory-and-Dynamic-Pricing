package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reorder_suggestions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer currentStock;

    private Integer recommendedQuantity;

    private Integer suggestedLeadTimeDays;

    private Double confidence;

    @Column(length = 1000)
    private String reasoning;

    @Enumerated(EnumType.STRING)
    private SuggestionStatus status = SuggestionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TriggerReason triggerReason;
}