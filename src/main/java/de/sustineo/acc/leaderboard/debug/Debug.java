package de.sustineo.acc.leaderboard.debug;

import lombok.extern.java.Log;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log
@Component
@Profile("debug")
public class Debug {
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException {
        log.warning("inject beans with @Autowired here and call them for debugging purposes");
    }
}
