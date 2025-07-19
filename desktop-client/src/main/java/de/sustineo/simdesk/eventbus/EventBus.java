package de.sustineo.simdesk.eventbus;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private static final List<EventListener> listeners = new ArrayList<>();
    private static final Object syncObject = new Object();

    public static void register(EventListener listener) {
        synchronized (syncObject) {
            listeners.add(listener);
        }
    }

    public static void unregister(EventListener listener) {
        synchronized (syncObject) {
            listeners.remove(listener);
        }
    }

    public static void publish(Event e) {
        synchronized (syncObject) {
            listeners.forEach(listener -> {
                listener.onEvent(e);
            });
        }
    }
}
