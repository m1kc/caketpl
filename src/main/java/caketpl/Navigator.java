package caketpl;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Navigator {
    private JsonNode current;

    public Navigator(JsonNode current) {
        this.current = current;
    }

    public JsonNode getCurrent() {
        return this.current;
    }

    public Navigator goKey(String key) {
        current = current.get(key);
        return this;
    }

    public Navigator goIndex(int index) {
        current = current.get(index);
        return this;
    }

    public static Iterable<NavigatorElement> parseTemplate(String s) throws ParseException {
        Pattern patternKey = Pattern.compile("^([a-zA-Z0-9_]+)([.]?)");
        Pattern patternIndex = Pattern.compile("^\\[([0-9]+)\\]([.]?)");
        Matcher m;

        ArrayList<NavigatorElement> ret = new ArrayList<>();

        while (true) {
            if (s.length() == 0) {
                return ret;
            }

            // index navigation?
            m = patternIndex.matcher(s);
            if (m.find()) {
                ret.add(new NavigatorElement(Integer.parseInt(m.group(1))));
                s = s.substring(m.end());
                continue;
            }

            // key navigation?
            m = patternKey.matcher(s);
            if (m.find()) {
                ret.add(new NavigatorElement(m.group(1)));
                s = s.substring(m.end());
                continue;
            }

            // I dunno, invalid
            throw new ParseException("Could not parse template near: `"+s+"`", 0);
        }
    }

    public Navigator goTemplate(String s) throws ParseException {
        Iterable<NavigatorElement> elements = parseTemplate(s);
        for (NavigatorElement el : elements) {
            el.executeOn(this);
        }
        return this;
    }
}
