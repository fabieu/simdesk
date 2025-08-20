package de.sustineo.simdesk.entities.livetiming.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LiveTimingEvent<T> extends ApplicationEvent {
    private final String dashboardId;

    protected LiveTimingEvent(T source, String dashboardId) {
        super(source);

        this.dashboardId = dashboardId;
    }
}
