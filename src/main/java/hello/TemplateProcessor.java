package hello;

import com.fasterxml.jackson.databind.JsonNode;

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

//    public static String render(String source, HashMap<String, JsonNode> params) {
//        Pattern r = Pattern.compile("\\$\\{(.*?)\\}");  // ${param}
//        Matcher m = r.matcher(source);
//
//        StringBuffer sb = new StringBuffer();
//
//        while (m.find()) {
//            String key = m.group(1);
//            m.appendReplacement(sb, params.get(key));
//        }
//        m.appendTail(sb);
//
//        return sb.toString();
//    }
}