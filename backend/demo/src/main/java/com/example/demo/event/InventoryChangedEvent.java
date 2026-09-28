package com.example.demo.event;

import com.example.demo.entity.Product;
import com.example.demo.entity.TriggerReason;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InventoryChangedEvent extends ApplicationEvent {
    private final Product product;
    private final TriggerReason triggerReason;
    private final int quantityChange;

    public InventoryChangedEvent(Object source, Product product, TriggerReason triggerReason, int quantityChange) {
        super(source);
        this.product = product;
        this.triggerReason = triggerReason;
        this.quantityChange = quantityChange;
    }
}