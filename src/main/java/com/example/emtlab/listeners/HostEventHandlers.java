package com.example.emtlab.listeners;

import com.example.emtlab.events.HostChangedEvent;
import com.example.emtlab.service.domain.HostService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class HostEventHandlers {

    private final HostService hostService;

    public HostEventHandlers(HostService hostService) {
        this.hostService = hostService;
    }

    @EventListener
    public void onHostChanged (HostChangedEvent event) {
        this.hostService.refreshMaterializedView();
    }
}
