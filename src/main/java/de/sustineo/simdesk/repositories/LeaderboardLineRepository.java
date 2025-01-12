package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.LeaderboardLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardLineRepository extends JpaRepository<LeaderboardLine, Long> {
    List<LeaderboardLine> findBySessionIdOrderByRanking(Long sessionId);
}
