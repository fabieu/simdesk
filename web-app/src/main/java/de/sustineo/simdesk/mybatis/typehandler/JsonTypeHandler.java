package de.sustineo.simdesk.mybatis.typehandler;

import de.sustineo.simdesk.utils.json.JsonClient;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    private final Class<T> type;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        String json = JsonClient.toJson(parameter);

        if (jdbcType == JdbcType.OTHER) {
            // Assume it's JSON/JSONB (PostgreSQL-specific)
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(json);
            ps.setObject(i, pgObject);
        } else {
            // Fallback for TEXT, VARCHAR, etc.
            ps.setString(i, json);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    private T parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        return JsonClient.fromJson(json, type);
    }
}
