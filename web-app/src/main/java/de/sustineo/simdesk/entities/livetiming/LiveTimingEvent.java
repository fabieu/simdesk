package de.sustineo.simdesk.entities.livetiming;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LiveTimingEvent<T> extends ApplicationEvent {
    private final String dashboardId;

    private LiveTimingEvent(T source, String dashboardId) {
        super(source);

        this.dashboardId = dashboardId;
    }

    public static <T> LiveTimingEvent<T> of(T source, String dashboardId) {
        return new LiveTimingEvent<>(source, dashboardId);
    }
}
