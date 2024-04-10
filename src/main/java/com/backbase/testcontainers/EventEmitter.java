package com.backbase.testcontainers;

import com.backbase.buildingblocks.backend.communication.event.EnvelopedEvent;
import com.backbase.buildingblocks.backend.communication.event.proxy.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventEmitter<T> {

    private final EventBus eventBus;

    @Autowired
    public EventEmitter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void sendMessage(T event) {
        EnvelopedEvent<T> envelopedEvent = new EnvelopedEvent<>();
        envelopedEvent.setEvent(event);
        eventBus.emitEvent(envelopedEvent);
    }
}
