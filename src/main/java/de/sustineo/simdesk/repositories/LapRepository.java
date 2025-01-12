package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.Lap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LapRepository extends JpaRepository<Lap, Long> {
    @Query(value = """
            select l from Lap l
            where l.session.id = :sessionId and l.driver.playerId in :playerIds
            order by l.id asc
            """)
    List<Lap> findBySessionAndPlayerIdsOrderByIdAsc(@Param("sessionId") Long sessionId, @Param("playerIds") List<String> playerIds);
}
