package svc

import caketpl.TemplateProcessor

import java.text.ParseException

import com.fasterxml.jackson.core.JsonProcessingException
import io.swagger.annotations.ApiModelProperty


class TemplateWithInfo(
    var model: Template, @field:ApiModelProperty(value = "Selectors used by this template")
    var selectors: Array<String>
){
    companion object {
        @Throws(ParseException::class, JsonProcessingException::class)
        fun from(t: Template): TemplateWithInfo {
            return TemplateWithInfo(t, TemplateProcessor.findSelectors(t.source))
        }
    }
}
