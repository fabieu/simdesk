package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
import de.sustineo.simdesk.entities.stewarding.StewardingEvent;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ProfileManager.PROFILE_STEWARDING)
@Component
@Mapper
public interface StewardingMapper {
    @Results(id = "stewardingEventResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "simulationId", column = "simulation_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "startDatetime", column = "start_datetime"),
            @Result(property = "endDatetime", column = "end_datetime"),
            @Result(property = "archiveDatetime", column = "archive_datetime"),
            @Result(property = "updateDatetime", column = "update_datetime"),
            @Result(property = "insertDatetime", column = "insert_datetime"),
    })
    @Select("""
            SELECT *
            FROM simdesk.stewarding_event
            WHERE archive_datetime IS NULL
            ORDER BY id
            """)
    @Select(databaseId = DatabaseVendor.SQLITE, value = """
            SELECT *
            FROM stewarding_event
            WHERE archive_datetime IS NULL
            ORDER BY id
            """)
    List<StewardingEvent> findAllActiveEvents();

    @Update("""
            UPDATE simdesk.stewarding_event
            SET archive_datetime = current_timestamp
            WHERE id = #{id}
            """)
    @Update(databaseId = DatabaseVendor.SQLITE, value = """
            UPDATE stewarding_event
            SET archive_datetime = current_timestamp
            WHERE id = #{id}
            """)
    void archiveEvent(StewardingEvent stewardingEvent);
}
