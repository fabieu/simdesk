package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.LeaderboardLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardLineRepository extends JpaRepository<LeaderboardLine, Long> {
    @Query("""
            SELECT l FROM LeaderboardLine l
            JOIN FETCH l.drivers d
            WHERE l.session.id = :sessionId
            ORDER BY l.ranking
            """)
    List<LeaderboardLine> findBySessionIdOrderByRanking(@Param("sessionId") Long sessionId);
}
