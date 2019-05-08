package svc

import io.swagger.annotations.ApiModelProperty

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Auto-generated template ID")
    var id: Long = 0

    @NotBlank(message = "Source is mandatory")
    @ApiModelProperty(notes = "Template source")
    var source: String? = null
}