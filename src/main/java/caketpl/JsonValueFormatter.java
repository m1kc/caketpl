package caketpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class JsonValueFormatter {
    public static String format(JsonNode value) throws JsonProcessingException {
        // If that's an array, output its contents separated by space
        if (value.isArray()) {
            ArrayList<String> ret = new ArrayList<>();
            for (int i=0; i<value.size(); i++) {
                ret.add(JsonValueFormatter.format(value.get(i)));
            }
            return String.join(" ", ret);
        }

        // If that's a primitive type, output as is
        if (value.isNumber() || value.isTextual()) {
            return value.asText();
        }

        // Otherwise, output as JSON
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(value);
    }
}
