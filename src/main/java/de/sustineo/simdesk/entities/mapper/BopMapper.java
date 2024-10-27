package de.sustineo.simdesk.entities.mapper;

import de.sustineo.simdesk.configuration.ProfileManager;
import de.sustineo.simdesk.entities.Bop;
import de.sustineo.simdesk.entities.database.DatabaseVendor;
import org.apache.ibatis.annotations.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ProfileManager.PROFILE_BOP)
@Component
@Mapper
public interface BopMapper {
    @Results(id = "bopResultMap", value = {
            @Result(property = "trackId", column = "track_id"),
            @Result(property = "carId", column = "car_id"),
            @Result(property = "ballastKg", column = "ballast_kg"),
            @Result(property = "restrictor", column = "restrictor"),
            @Result(property = "active", column = "active"),
            @Result(property = "username", column = "username"),
            @Result(property = "updateDatetime", column = "update_datetime"),
    })
    @Select("SELECT * FROM simdesk.bop")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM bop")
    List<Bop> findAll();


    @ResultMap("bopResultMap")
    @Select("SELECT * FROM simdesk.bop WHERE active = true")
    @Select(databaseId = DatabaseVendor.SQLITE, value = "SELECT * FROM bop WHERE active = true")
    List<Bop> findActive();

    @Insert("""
            INSERT INTO simdesk.bop (track_id, car_id, restrictor, ballast_kg, username, active, update_datetime)
            VALUES (#{trackId}, #{carId}, #{restrictor}, #{ballastKg}, #{username}, #{active}, #{updateDatetime})
            """)
    @Insert(databaseId = DatabaseVendor.SQLITE, value = """
            INSERT INTO bop (track_id, car_id, restrictor, ballast_kg, username, active, update_datetime)
            VALUES (#{trackId}, #{carId}, #{restrictor}, #{ballastKg}, #{username}, #{active}, #{updateDatetime})
            """)
    void insert(Bop bop);


    @Update("""
            UPDATE simdesk.bop SET restrictor = #{restrictor}, ballast_kg = #{ballastKg}, username = #{username}, active = #{active}, update_datetime = #{updateDatetime}
            WHERE track_id = #{trackId} AND car_id = #{carId}
            """)
    @Update(databaseId = DatabaseVendor.SQLITE, value = """
            UPDATE bop SET restrictor = #{restrictor}, ballast_kg = #{ballastKg}, username = #{username}, active = #{active}, update_datetime = #{updateDatetime}
            WHERE track_id = #{trackId} AND car_id = #{carId}
            """)
    void update(Bop bop);

    @Update("""
            UPDATE simdesk.bop SET active = true, update_datetime = now()
            WHERE track_id = #{trackId}
            """)
    @Update(databaseId = DatabaseVendor.SQLITE, value = """
            UPDATE bop SET active = true, update_datetime = now()
            WHERE track_id = #{trackId}
            """)
    void enableAllForTrack(String trackId);

    @Update("""
            UPDATE simdesk.bop SET active = false, update_datetime = now()
            WHERE track_id = #{trackId}
            """)
    @Update(databaseId = DatabaseVendor.SQLITE, value = """
            UPDATE bop SET active = false, update_datetime = now()
            WHERE track_id = #{trackId}
            """)
    void disableAllForTrack(String trackId);

    @Update("""
            UPDATE simdesk.bop SET restrictor = 0, ballast_kg = 0, update_datetime = now()
            WHERE track_id = #{trackId}
            """)
    @Update(databaseId = DatabaseVendor.SQLITE, value = """
            UPDATE bop SET restrictor = 0, ballast_kg = 0, update_datetime = now()
            WHERE track_id = #{trackId}
            """)
    void resetAllForTrack(String trackId);
}
