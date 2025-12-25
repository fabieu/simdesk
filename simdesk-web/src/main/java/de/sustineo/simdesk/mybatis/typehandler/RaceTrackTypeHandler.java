package de.sustineo.simdesk.mybatis.typehandler;

import de.sustineo.simdesk.entities.RaceTrack;
import de.sustineo.simdesk.entities.RaceTracks;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(RaceTrack.class)
@MappedJdbcTypes(JdbcType.VARCHAR) // or INTEGER, depending on your column type
public class RaceTrackTypeHandler extends BaseTypeHandler<RaceTrack> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RaceTrack raceTrack, JdbcType jdbcType) throws SQLException {
        ps.setString(i, raceTrack.getGlobalId());
    }

    @Override
    public RaceTrack getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return RaceTracks.getById(rs.getString(columnName));
    }

    @Override
    public RaceTrack getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return RaceTracks.getById(rs.getString(columnIndex));
    }

    @Override
    public RaceTrack getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return RaceTracks.getById(cs.getString(columnIndex));
    }
}