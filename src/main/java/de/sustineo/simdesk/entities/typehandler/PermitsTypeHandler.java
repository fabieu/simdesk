package de.sustineo.simdesk.entities.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import de.sustineo.simdesk.utils.json.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class PermitsTypeHandler extends BaseTypeHandler<Set<String>> {
    TypeReference<LinkedHashSet<String>> setStringType = new TypeReference<>() {
    };

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Set<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            if (parameter == null) {
                ps.setString(i, null);
            } else {
                ps.setString(i, JsonUtils.toJson(parameter));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String content = rs.getString(columnName);

            if (content == null) {
                return null;
            }

            return JsonUtils.fromJson(content, setStringType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            String content = rs.getString(columnIndex);

            if (content == null) {
                return null;
            }

            return JsonUtils.fromJson(content, setStringType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            String content = cs.getString(columnIndex);

            if (content == null) {
                return null;
            }

            return JsonUtils.fromJson(content, setStringType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
