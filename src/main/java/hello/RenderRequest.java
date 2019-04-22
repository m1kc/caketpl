package hello;

import java.util.HashMap;

public class RenderRequest {
    private final String source;
    private final HashMap<String, String> params;

    public RenderRequest(String source, HashMap<String, String> params) {
        this.source = source;
        this.params = params;
    }

    public String getSource() {
        return source;
    }

    public HashMap<String, String> getParams() {
        return params;
    }
}
