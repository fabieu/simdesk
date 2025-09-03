package de.sustineo.simdesk.entities.livetiming.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LiveTimingEvent<T> extends ApplicationEvent {
    private final String dashboardId;

    protected LiveTimingEvent(String dashboardId, T source) {
        super(source);

        this.dashboardId = dashboardId;
    }
}