package de.sustineo.acc.leaderboard.debug;

import de.sustineo.acc.leaderboard.entities.json.Session;
import de.sustineo.acc.leaderboard.services.SessionService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Log
@Component
public class Debug {
    @Autowired
    SessionService sessionService;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException {
        Optional<Session> session = sessionService.readSession(Path.of("src/main/resources/examples/221008_224809_R.json"));
        log.info(session.get().toString());
        log.severe("inject beans with @Autowired here and call them for debugging purposes");
    }
}
