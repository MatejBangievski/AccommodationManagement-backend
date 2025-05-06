package com.example.emtlab.events;

import com.example.emtlab.model.enumerations.EntityChangeType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class HostChangedEvent extends ApplicationEvent {

    private final EntityChangeType changeType;
    private final LocalDateTime when;

    public HostChangedEvent(Object source, EntityChangeType changeType) {
        super(source);
        this.changeType = changeType;
        this.when = LocalDateTime.now();
    }

    public HostChangedEvent(Object source, EntityChangeType changeType, LocalDateTime when) {
        super(source);
        this.changeType = changeType;
        this.when = when;
    }
}
