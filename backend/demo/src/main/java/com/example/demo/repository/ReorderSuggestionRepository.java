package com.example.demo.repository;

import com.example.demo.entity.ReorderSuggestion;
import com.example.demo.entity.SuggestionStatus;
import com.example.demo.entity.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, Long> {
    List<ReorderSuggestion> findByStatus(SuggestionStatus status);
    
    List<ReorderSuggestion> findByProductIdAndStatusAndTriggerReason(
        Long productId, 
        SuggestionStatus status, 
        TriggerReason triggerReason
    );
}