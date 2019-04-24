package svc;

import caketpl.TemplateProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.ParseException;

public class TemplateWithInfo {
    public Template model;
    public String[] selectors;

    public TemplateWithInfo(Template model, String[] selectors) {
        this.model = model;
        this.selectors = selectors;
    }

    public static TemplateWithInfo from(Template t) throws ParseException, JsonProcessingException {
        return new TemplateWithInfo(t, TemplateProcessor.findSelectors(t.getSource()));
    }
}
