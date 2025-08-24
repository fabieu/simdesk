package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.CarGroup;
import de.sustineo.simdesk.entities.json.kunos.acc.enums.AccCar;
import de.sustineo.simdesk.entities.ranking.DriverBestSectors;
import de.sustineo.simdesk.entities.ranking.DriverRanking;
import de.sustineo.simdesk.entities.ranking.GroupRanking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Mapper
public interface RankingMapper {
    @Results(id = "groupRankingResultMap", value = {
            @Result(property = "carGroup", column = "car_group"),
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "driver.id", column = "driver_id"),
            @Result(property = "driver.firstName", column = "first_name"),
            @Result(property = "driver.lastName", column = "last_name"),
            @Result(property = "driver.shortName", column = "short_name"),
            @Result(property = "driver.visibility", column = "visibility"),
            @Result(property = "trackId", column = "track_id"),
    })
    @Select(databaseId = "sqlite", value = """
            SELECT lap.car_group, lap.car_model_id, lap.lap_time_millis, driver.*, session.track_id
            FROM lap
                    INNER JOIN session ON lap.session_id = session.id
                    INNER JOIN driver ON lap.driver_id = driver.driver_id
                    INNER JOIN (
                    SELECT lap.car_model_id, lap.driver_id, session.track_id, lap.car_group, MIN(lap.lap_time_millis) AS min_lap_time_millis
                        FROM lap
                            INNER JOIN session ON lap.session_id = session.id
                            INNER JOIN driver ON lap.driver_id = driver.driver_id
                        WHERE lap.valid IS TRUE
                          AND driver.visibility = 'PUBLIC'
                          AND session.session_datetime >= #{from}
                          AND session.session_datetime <= #{to}
                        GROUP BY session.track_id, lap.car_group
                    ) fastest_laps
                    ON lap.car_model_id = fastest_laps.car_model_id
                       AND lap.driver_id = fastest_laps.driver_id
                       AND session.track_id = fastest_laps.track_id
                       AND lap.car_group = fastest_laps.car_group
                       AND lap.lap_time_millis = fastest_laps.min_lap_time_millis
                    WHERE lap.valid IS TRUE
                      AND driver.visibility = 'PUBLIC'
                      AND session.session_datetime >= #{from}
                      AND session.session_datetime <= #{to}
            ORDER BY lap.car_group, session.track_id;
            """)
    @Select(databaseId = "postgres", value = """
            SELECT DISTINCT ON (session.track_id, lap.car_group)
                   lap.car_group,
                   lap.car_model_id,
                   lap.lap_time_millis,
                   driver.*,
                   session.track_id
            FROM lap
                     INNER JOIN session ON lap.session_id = session.id
                     INNER JOIN driver ON lap.driver_id = driver.driver_id
            WHERE lap.valid IS TRUE
              AND driver.visibility = 'PUBLIC'
              AND session.session_datetime >= #{from}
              AND session.session_datetime <= #{to}
            ORDER BY lap.car_group, session.track_id, lap.lap_time_millis;
            """)
    List<GroupRanking> findAllTimeFastestLaps(Instant from, Instant to);

    @Results(id = "driverRankingResultMap", value = {
            @Result(property = "carModelId", column = "car_model_id"),
            @Result(property = "lapTimeMillis", column = "lap_time_millis"),
            @Result(property = "sector1Millis", column = "sector1_millis"),
            @Result(property = "sector2Millis", column = "sector2_millis"),
            @Result(property = "sector3Millis", column = "sector3_millis"),
            @Result(property = "driver.id", column = "driver_id"),
            @Result(property = "driver.firstName", column = "first_name"),
            @Result(property = "driver.lastName", column = "last_name"),
            @Result(property = "driver.shortName", column = "short_name"),
            @Result(property = "driver.visibility", column = "visibility"),
            @Result(property = "session.sessionType", column = "session_type"),
            @Result(property = "session.trackId", column = "track_id"),
            @Result(property = "session.serverName", column = "server_name"),
            @Result(property = "session.sessionDatetime", column = "session_datetime"),
            @Result(property = "session.fileChecksum", column = "file_checksum"),
    })
    @Select(databaseId = "sqlite", value = """
            <script>
            SELECT lap.*, driver.*, session.*
            FROM lap
                     INNER JOIN session ON lap.session_id = session.id
                     INNER JOIN driver ON lap.driver_id = driver.driver_id
                     INNER JOIN (SELECT lap.driver_id, MIN(lap.lap_time_millis) AS lap_time_millis
                                 FROM lap
                                    INNER JOIN session ON lap.session_id = session.id
                                    INNER JOIN driver ON lap.driver_id = driver.driver_id
                                 WHERE lap.valid = TRUE
                                   AND lap.car_group = #{carGroup}
                                   <if test='car != null'>
                                   AND lap.car_model_id = #{car.id}
                                   </if>
                                   AND session.track_id = #{trackId}
                                   AND driver.visibility = 'PUBLIC'
                                   AND session.session_datetime <![CDATA[>=]]> #{from}
                                   AND session.session_datetime <![CDATA[<=]]> #{to}
                                 GROUP BY lap.driver_id) fastest_laps
                                ON lap.driver_id = fastest_laps.driver_id
                                    AND lap.lap_time_millis = fastest_laps.lap_time_millis
            WHERE lap.valid = TRUE
              AND lap.car_group = #{carGroup}
              AND session.track_id = #{trackId}
              AND driver.visibility = 'PUBLIC'
              AND session.session_datetime <![CDATA[>=]]> #{from}
              AND session.session_datetime <![CDATA[<=]]> #{to}
            </script>
            """)
    @Select(databaseId = "postgres", value = """
            <script>
            SELECT DISTINCT ON (lap.driver_id)
                   lap.*,
                   driver.*,
                   session.*
            FROM lap
                     INNER JOIN session ON lap.session_id = session.id
                     INNER JOIN driver ON lap.driver_id = driver.driver_id
            WHERE lap.valid = TRUE
              AND lap.car_group = #{carGroup}
              <if test='car != null'>
              AND lap.car_model_id = #{car.id}
              </if>
              AND session.track_id = #{trackId}
              AND driver.visibility = 'PUBLIC'
              AND session.session_datetime <![CDATA[>=]]> #{from}
              AND session.session_datetime <![CDATA[<=]]> #{to}
            ORDER BY lap.driver_id, lap.lap_time_millis;
            </script>
            """)
    List<DriverRanking> findAllTimeFastestLapsByTrack(CarGroup carGroup, String trackId, Instant from, Instant to, AccCar car);

    @Results(id = "driverBestSectors", value = {
            @Result(property = "driverId", column = "driver_id"),
            @Result(property = "bestSector1Millis", column = "best_sector1_millis"),
            @Result(property = "bestSector2Millis", column = "best_sector2_millis"),
            @Result(property = "bestSector3Millis", column = "best_sector3_millis"),
    })
    @Select(value = """
            <script>
            SELECT
                lap.driver_id,
                MIN(CASE WHEN lap.sector1_millis > 0 THEN lap.sector1_millis END) AS best_sector1_millis,
                MIN(CASE WHEN lap.sector2_millis > 0 THEN lap.sector2_millis END) AS best_sector2_millis,
                MIN(CASE WHEN lap.sector3_millis > 0 THEN lap.sector3_millis END) AS best_sector3_millis
            FROM lap
            INNER JOIN session ON lap.session_id = session.id
            WHERE lap.valid = TRUE
              AND lap.car_group = #{carGroup}
              <if test='car != null'>
              AND lap.car_model_id = #{car.id}
              </if>
              AND session.track_id = #{trackId}
              AND session.session_datetime <![CDATA[>=]]> #{from}
              AND session.session_datetime <![CDATA[<=]]> #{to}
            GROUP BY lap.driver_id
            </script>
            """)
    List<DriverBestSectors> findBestSectorsByTrack(CarGroup carGroup, String trackId, Instant from, Instant to, AccCar car);
}
