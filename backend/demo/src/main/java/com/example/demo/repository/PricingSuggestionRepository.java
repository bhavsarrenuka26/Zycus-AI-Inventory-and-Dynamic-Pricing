package com.example.demo.repository;

import com.example.demo.entity.PricingSuggestion;
import com.example.demo.entity.SuggestionStatus;
import com.example.demo.entity.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PricingSuggestionRepository extends JpaRepository<PricingSuggestion, Long> {
    List<PricingSuggestion> findByStatus(SuggestionStatus status);
    
    List<PricingSuggestion> findByProductIdAndStatusAndTriggerReason(
        Long productId, 
        SuggestionStatus status, 
        TriggerReason triggerReason
    );
}