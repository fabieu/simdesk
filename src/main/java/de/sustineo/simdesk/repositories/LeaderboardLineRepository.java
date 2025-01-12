package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.LeaderboardLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardLineRepository extends JpaRepository<LeaderboardLine, Long> {
}
