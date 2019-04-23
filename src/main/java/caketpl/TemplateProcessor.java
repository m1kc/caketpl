package caketpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateProcessor {
    public static String process(String source, HashMap<String, String> params) {
        Pattern r = Pattern.compile("\\$\\{(.*?)\\}");  // ${param}
        Matcher m = r.matcher(source);

        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String key = m.group(1);
            m.appendReplacement(sb, params.get(key));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    public static String render(String source, JsonNode params) throws ParseException, JsonProcessingException {
        Pattern r = Pattern.compile("\\$\\{(.*?)\\}");  // ${param}
        Matcher m = r.matcher(source);

        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String path = m.group(1);
            Navigator nav = new Navigator(params);
            m.appendReplacement(
                    sb,
                    JsonValueFormatter.format(nav.goTemplate(path).getCurrent())
            );
        }
        m.appendTail(sb);

        return sb.toString();
    }
}