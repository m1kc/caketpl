package svc;

import caketpl.TemplateProcessor;

import java.text.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiModelProperty;


public class TemplateWithInfo {
    public Template model;
    @ApiModelProperty(value = "Selectors used by this template")
    public String[] selectors;

    public TemplateWithInfo(Template model, String[] selectors) {
        this.model = model;
        this.selectors = selectors;
    }

    public static TemplateWithInfo from(Template t) throws ParseException, JsonProcessingException {
        return new TemplateWithInfo(t, TemplateProcessor.findSelectors(t.getSource()));
    }
}
