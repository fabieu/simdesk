package de.sustineo.acc.servertools.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Pattern;

public class BooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Pattern truePattern = Pattern.compile("^true|1|yes|y$", Pattern.CASE_INSENSITIVE);
        Pattern falsePattern = Pattern.compile("^false|0|no|n$", Pattern.CASE_INSENSITIVE);

        String text = p.getValueAsString();
        if (StringUtils.isBlank(text)) {
            return null;
        }

        if (truePattern.matcher(text).matches()) {
            return true;
        }

        if (falsePattern.matcher(text).matches()) {
            return false;
        }

        return null;
    }
}
