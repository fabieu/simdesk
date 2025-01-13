package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByFileChecksum(String fileChecksum);

    List<Session> findBySessionDatetimeBetweenOrderBySessionDatetimeDesc(Instant startTime, Instant endTime);
}
