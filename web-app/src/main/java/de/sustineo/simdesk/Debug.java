package de.sustineo.simdesk;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log
@Profile("debug")
@Component
@RequiredArgsConstructor
public class Debug {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        log.info("Debug profile is active!");
    }
}
