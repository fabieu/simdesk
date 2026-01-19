package de.sustineo.simdesk.utils.json;

import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.regex.Pattern;

public class BooleanDeserializer extends ValueDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) {
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
