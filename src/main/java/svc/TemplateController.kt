package svc

import caketpl.TemplateProcessor

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import javassist.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.util.Optional
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


@RestController
@Api(value = "Templates")
class TemplateController {

    @Autowired
    private val templateRepository: TemplateRepository? = null

    private val counter = AtomicLong()
    private val results: Cache<Long, Optional<String>>

    init {
        this.results = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build()
    }

    private fun json(): ObjectNode {
        return ObjectMapper().createObjectNode()
    }

    // CRUD

    @GetMapping("/template/{id}")
    @ApiOperation(value = "Get template by ID")
    @Throws(Exception::class)
    operator fun get(@PathVariable id: Long): TemplateWithInfo {
        val value = templateRepository!!.findById(id)

        return if (!value.isPresent) {
            throw ObjectNotFoundException()
        } else {
            TemplateWithInfo.from(value.get())
        }
    }

    @PostMapping("/template")
    @ApiOperation(value = "Create template")
    @Throws(Exception::class)
    fun create(@RequestBody @Valid template: Template): TemplateWithInfo {
        var template = template
        if (!TemplateProcessor.isValid(template.source)) {
            throw InvalidTemplateException()
        }

        template = templateRepository!!.save(template)
        return TemplateWithInfo.from(template)
    }

    @DeleteMapping("/template/{id}")
    @ApiOperation(value = "Delete template")
    fun delete(@PathVariable("id") id: Long): ObjectNode {
        templateRepository!!.deleteById(id)
        return json().put("result", "ok")
    }

    @PutMapping("/template/{id}")
    @ApiOperation(value = "Change template")
    @Throws(Exception::class)
    fun update(@PathVariable("id") id: Long, @RequestBody @Valid newTemplate: Template): TemplateWithInfo {
        var newTemplate = newTemplate
        val value = templateRepository!!.findById(id)

        if (!value.isPresent) {
            throw ObjectNotFoundException()
        }

        if (!TemplateProcessor.isValid(newTemplate.source)) {
            throw InvalidTemplateException()
        }

        newTemplate.id = id
        newTemplate = templateRepository.save(newTemplate)
        return TemplateWithInfo.from(newTemplate)
    }

    // Rendering

    @PostMapping("/template/{id}/render")
    @ApiOperation(value = "Render template")
    @Throws(Exception::class)
    fun render(@PathVariable("id") id: Long, @RequestBody params: JsonNode): JsonNode {
        val template = templateRepository!!.findById(id).orElseThrow { NotFoundException("Cannot find template") }
        return json().put("result", TemplateProcessor.render(template.source, params))
    }

    // Async render

    @PostMapping("/template/{id}/render/async")
    @ApiOperation(value = "Render template asynchronously")
    fun renderAsync(@PathVariable("id") id: Long, @RequestBody params: JsonNode): JsonNode {
        val requestID = counter.incrementAndGet()
        this.results.put(requestID, Optional.empty())

        Thread {
            try {
                Thread.sleep(5000L)  // artificial delay

                val template = templateRepository!!.findById(id).orElseThrow { NotFoundException("Cannot find template") }
                val result = TemplateProcessor.render(template.source, params)
                this.results.put(requestID, Optional.of(result))
            } catch (ex: Throwable) {
                this.results.put(requestID, Optional.of(ex.toString()))
            }
        }.start()

        return json().put("request-id", requestID)
    }

    @GetMapping("/template/render-result/{id}")
    @ApiOperation(value = "Query results of async render")
    fun renderAsyncResult(@PathVariable("id") rid: Long): JsonNode {
        val value = this.results.getIfPresent(rid) ?: return json().put("status", "Not found or expired")

        return if (value.isPresent) {
            json().put("result", value.get()).put("status", "ok")
        } else {
            json().put("status", "In progress")
        }
    }
}