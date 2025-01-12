package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    List<Penalty> findBySessionIdAndCarIdOrderByIdDesc(Long sessionId, Integer carId);
}
