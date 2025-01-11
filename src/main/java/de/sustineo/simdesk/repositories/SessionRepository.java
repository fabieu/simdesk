package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByFileChecksum(String fileChecksum);

    List<Session> findBySessionDatetimeBetween(Instant startTime, Instant endTime);
}
