package caketpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TemplateProcessor provides methods for working with templates.
public class TemplateProcessor {

    // findSelectors returns a list of all selectors mentioned in the template.
    public static String[] findSelectors(String source) throws ParseException, JsonProcessingException {
        HashSet<String> selectors = new HashSet<>();

        Pattern r = Pattern.compile("\\$\\{(.*?)\\}");  // ${param}
        Matcher m = r.matcher(source);

        while (m.find()) {
            String path = m.group(1);
            selectors.add(path);
        }

        String[] ret = new String[selectors.size()];
        selectors.toArray(ret);
        Arrays.sort(ret);
        return ret;
    }

    // isValid validates all selectors in a template.
    public static boolean isValid(String source) {
        try {
            String[] selectors = findSelectors(source);
            for (String selector : selectors) {
                Navigator.Companion.parseTemplate(selector);
            }
            return true;
        } catch (ParseException ex) {
            return false;
        } catch (JsonProcessingException ex) {
            return false;
        }
    }

    // render renders a template with the given params.
    public static String render(String source, JsonNode params) throws ParseException, JsonProcessingException {
        Pattern r = Pattern.compile("\\$\\{(.*?)\\}");  // ${param}
        Matcher m = r.matcher(source);

        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String path = m.group(1);
            Navigator nav = new Navigator(params);
            m.appendReplacement(
                    sb,
                    JsonValueFormatter.INSTANCE.format(nav.goTemplate(path).getCurrent())
            );
        }
        m.appendTail(sb);

        return sb.toString();
    }

}