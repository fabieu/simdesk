package de.sustineo.simdesk.repositories;

import de.sustineo.simdesk.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    List<Driver> findAllByOrderByLastActivityDesc();

    Driver findByPlayerId(String playerId);

    @Modifying
    @Query(value = """
            UPDATE Driver d
            SET d.visibility = COALESCE(:#{#driver.visibility}, d.visibility)
            WHERE d.playerId = :#{#driver.playerId}
            """)
    void updateVisibility(@Param("driver") Driver driver);

    @Query(value = """
            SELECT driver.*,
                   leaderboard_driver.drive_time_millis,
                   COUNT(CASE WHEN lap.valid THEN 1 END)     AS valid_lap_count,
                   COUNT(CASE WHEN NOT lap.valid THEN 1 END) AS invalid_lap_count
            FROM driver
                     INNER JOIN leaderboard_driver ON driver.player_id = leaderboard_driver.player_id
                     INNER JOIN lap ON (leaderboard_driver.session_id = lap.session_id AND leaderboard_driver.player_id = lap.driver_id)
            WHERE leaderboard_driver.car_id = #{carId}
              AND leaderboard_driver.session_id = #{sessionId}
            GROUP BY driver.player_id, leaderboard_driver.drive_time_millis;
            """, nativeQuery = true)
    List<Driver> findBySessionIdAndCarId(@Param("sessionId") Integer sessionId, @Param("carId") Integer carId);
}
