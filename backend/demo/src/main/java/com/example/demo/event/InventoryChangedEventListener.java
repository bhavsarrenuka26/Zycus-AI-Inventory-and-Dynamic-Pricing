package com.example.demo.event;

import com.example.demo.entity.TriggerReason;
import com.example.demo.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryChangedEventListener {
    
    private final SuggestionService suggestionService;
    
    @Async
    @TransactionalEventListener
    public void handleInventoryChangedEvent(InventoryChangedEvent event) {
        try {
            log.info("Processing inventory changed event for product {}", event.getProduct().getId());
            
            // Generate pricing suggestion
            suggestionService.generatePricingSuggestion(
                event.getProduct().getId(), 
                event.getTriggerReason()
            );
            
            // Generate reorder suggestion if inventory is low
            if (event.getTriggerReason() == TriggerReason.INVENTORY_LOW || 
                event.getProduct().getStockLevel() < event.getProduct().getReorderThreshold()) {
                suggestionService.generateReorderSuggestion(
                    event.getProduct().getId(), 
                    TriggerReason.INVENTORY_LOW
                );
            }
            
            log.info("Completed processing inventory changed event for product {}", event.getProduct().getId());
        } catch (Exception e) {
            log.error("Error processing inventory changed event for product {}: {}", 
                     event.getProduct().getId(), e.getMessage(), e);
        }
    }
}