package de.sustineo.simdesk.mybatis.mapper;

import de.sustineo.simdesk.entities.stewarding.StewardingSession;
import de.sustineo.simdesk.mybatis.typehandler.RaceTrackTypeHandler;
import de.sustineo.simdesk.mybatis.typehandler.StewardingEntrylistTypeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface StewardingSessionMapper {
    @Results(id = "stewardingSessionResultMap", value = {
            @Result(id = true, property = "id", column = "session_id"),
            @Result(property = "title", column = "session_title"),
            @Result(property = "description", column = "session_description"),
            @Result(property = "startDatetime", column = "session_start_datetime"),
            @Result(property = "endDatetime", column = "session_end_datetime"),
            @Result(property = "updateDatetime", column = "session_update_datetime"),
            @Result(property = "raceTrack", column = "session_race_track_id", typeHandler = RaceTrackTypeHandler.class),
            @Result(property = "entrylist", column = "entrylist", typeHandler = StewardingEntrylistTypeHandler.class),
    })
    @Select("""
            SELECT
                session.id AS session_id,
                session.title AS session_title,
                session.description AS session_description,
                session.start_datetime AS session_start_datetime,
                session.end_datetime AS session_end_datetime,
                session.update_datetime AS session_update_datetime,
                session.race_track_id AS session_race_track_id,
                session.entrylist AS session_entrylist
            FROM stewarding_session session
            LEFT JOIN stewarding_penalty_sets penalty_sets ON penalty_sets.id = session.penalty_set_id;
            """)
    StewardingSession findAll();
}
