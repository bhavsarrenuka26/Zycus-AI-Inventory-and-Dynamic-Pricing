package com.example.demo.controller;

import com.example.demo.dto.SuggestionStatusUpdateRequest;
import com.example.demo.entity.PricingSuggestion;
import com.example.demo.entity.ReorderSuggestion;
import com.example.demo.entity.SuggestionStatus;
import com.example.demo.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SuggestionController {
    
    private final SuggestionService suggestionService;
    
    @GetMapping("/pricing-suggestions")
    public List<PricingSuggestion> getAllPricingSuggestions() {
        return suggestionService.getAllPricingSuggestions();
    }
    
    @GetMapping("/reorder-suggestions")
    public List<ReorderSuggestion> getAllReorderSuggestions() {
        return suggestionService.getAllReorderSuggestions();
    }
    
    @GetMapping("/pricing-suggestions/pending")
    public List<PricingSuggestion> getPendingPricingSuggestions() {
        return suggestionService.getPendingPricingSuggestions();
    }
    
    @GetMapping("/reorder-suggestions/pending")
    public List<ReorderSuggestion> getPendingReorderSuggestions() {
        return suggestionService.getPendingReorderSuggestions();
    }
    
    @PatchMapping("/pricing-suggestions/{id}")
    public ResponseEntity<PricingSuggestion> updatePricingSuggestionStatus(
            @PathVariable Long id, 
            @RequestBody SuggestionStatusUpdateRequest request) {
        try {
            SuggestionStatus status = SuggestionStatus.valueOf(request.getStatus());
            PricingSuggestion updatedSuggestion = suggestionService.updatePricingSuggestionStatus(id, status);
            return ResponseEntity.ok(updatedSuggestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/reorder-suggestions/{id}")
    public ResponseEntity<ReorderSuggestion> updateReorderSuggestionStatus(
            @PathVariable Long id, 
            @RequestBody SuggestionStatusUpdateRequest request) {
        try {
            SuggestionStatus status = SuggestionStatus.valueOf(request.getStatus());
            ReorderSuggestion updatedSuggestion = suggestionService.updateReorderSuggestionStatus(id, status);
            return ResponseEntity.ok(updatedSuggestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}