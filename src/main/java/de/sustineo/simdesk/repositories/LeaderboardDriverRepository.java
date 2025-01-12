package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.LeaderboardDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardDriverRepository extends JpaRepository<LeaderboardDriver, Long> {
}
