package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Session;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(ProfileManager.PROFILE_LEADERBOARD)
@Component
@Mapper
public interface SessionMapper {
    @Results(id = "sessionResultMap", value = {
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "sessionType", column = "session_type"),
            @Result(property = "raceWeekendIndex", column = "race_weekend_index"),
            @Result(property = "serverName", column = "server_name"),
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "wetSession", column = "wet_session"),
            @Result(property = "carCount", column = "car_count"),
            @Result(property = "sessionDatetime", column = "session_datetime"),
            @Result(property = "fileChecksum", column = "file_checksum"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "fileDirectory", column = "file_directory"),
            @Result(property = "fileContent", column = "file_content"),
    })
    @Select("SELECT * FROM simdesk.session WHERE id = #{id} LIMIT 1")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM session WHERE id = #{id} LIMIT 1")
    Session findById(Integer id);
}
