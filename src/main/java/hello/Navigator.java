package hello;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
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
//        System.out.println("goKey: "+key);
        current = current.get(key);
        return this;
    }

    public Navigator goIndex(int index) {
//        System.out.println("goIndex: "+index);
        current = current.get(index);
        return this;
    }

    public Navigator goTemplate(String s) throws ParseException {
        while (true) {
            if (s.length() == 0) {
                return this;
            }

            Pattern patternKey = Pattern.compile("^([a-zA-Z]+)([.]?)");
            Pattern patternIndex = Pattern.compile("^\\[([0-9]+)\\]([.]?)");
            Matcher m;

            // index navigation?
            m = patternIndex.matcher(s);
            if (m.find()) {
                this.goIndex(Integer.parseInt(m.group(1)));
                s = s.substring(m.end());
                continue;
            }

            // key navigation?
            m = patternKey.matcher(s);
            if (m.find()) {
                this.goKey(m.group(1));
                s = s.substring(m.end());
                continue;
            }

            // I dunno, invalid
            throw new ParseException("Could not parse template near: `"+s+"`", 0);
        }
    }
}
