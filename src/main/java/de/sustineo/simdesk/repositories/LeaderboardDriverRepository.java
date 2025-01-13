package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.LeaderboardDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardDriverRepository extends JpaRepository<LeaderboardDriver, Long> {
    List<LeaderboardDriver> findBySessionIdAndCarId(Long sessionId, Integer carId);
}
