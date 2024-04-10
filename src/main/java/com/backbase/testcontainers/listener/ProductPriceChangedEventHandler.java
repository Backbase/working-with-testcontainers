package com.backbase.testcontainers.listener;

import com.backbase.buildingblocks.backend.communication.event.EnvelopedEvent;
import com.backbase.buildingblocks.backend.communication.event.handler.EventHandler;
import com.backbase.testcontainers.repository.ProductRepository;
import com.backbase.testcontainerssamples.event.spec.v1.ProductPriceChangedEvent;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ProductPriceChangedEventHandler implements EventHandler<ProductPriceChangedEvent> {

    private final ProductRepository productRepository;

    @Autowired
    public ProductPriceChangedEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void handle(
        EnvelopedEvent<ProductPriceChangedEvent> envelopedEvent) {
        ProductPriceChangedEvent event = envelopedEvent.getEvent();
        productRepository.updateProductPrice(event.getCode(), new BigDecimal(event.getPrice()));
    }
}
