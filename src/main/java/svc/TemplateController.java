package svc;

import caketpl.TemplateProcessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@Api(value="Templates")
public class TemplateController {

    @Autowired
    private TemplateRepository templateRepository;

    private final AtomicLong counter = new AtomicLong();
    private Cache<Long, Optional<String>> results;

    public TemplateController() {
        this.results = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    private static ObjectNode json() {
        return new ObjectMapper().createObjectNode();
    }

    // CRUD

    @GetMapping("/template/{id}")
    @ApiOperation(value = "Get template by ID")
    public TemplateWithInfo get(@PathVariable long id) throws Exception {
        Optional<Template> value = templateRepository.findById(id);

        if (!value.isPresent()) {
            throw new ObjectNotFoundException();
        } else {
            return TemplateWithInfo.from(value.get());
        }
    }

    @PostMapping("/template")
    @ApiOperation(value = "Create template")
    public TemplateWithInfo create(@RequestBody @Valid Template template) throws Exception {
        if (!TemplateProcessor.isValid(template.getSource())) {
            throw new InvalidTemplateException();
        }

        template = templateRepository.save(template);
        return TemplateWithInfo.from(template);
    }

    @DeleteMapping("/template/{id}")
    @ApiOperation(value = "Delete template")
    public ObjectNode delete(@PathVariable("id") long id) {
        templateRepository.deleteById(id);
        return json().put("result", "ok");
    }

    @PutMapping("/template/{id}")
    @ApiOperation(value = "Change template")
    public TemplateWithInfo update(@PathVariable("id") long id, @RequestBody @Valid Template newTemplate) throws Exception {
        Optional<Template> value = templateRepository.findById(id);

        if (!value.isPresent()) {
            throw new ObjectNotFoundException();
        }

        if (!TemplateProcessor.isValid(newTemplate.getSource())) {
            throw new InvalidTemplateException();
        }

        newTemplate.setId(id);
        newTemplate = templateRepository.save(newTemplate);
        return TemplateWithInfo.from(newTemplate);
    }

    // Rendering

    @PostMapping("/template/{id}/render")
    @ApiOperation(value = "Render template")
    public JsonNode render(@PathVariable("id") long id, @RequestBody JsonNode params) throws Exception {
        Template template = templateRepository.findById(id).orElseThrow(() -> new NotFoundException("Cannot find template"));
        return json().put("result", TemplateProcessor.render(template.getSource(), params));
    }

    // Async render

    @PostMapping("/template/{id}/render/async")
    @ApiOperation(value = "Render template asynchronously")
    public JsonNode renderAsync(@PathVariable("id") long id, @RequestBody JsonNode params) {
        long requestID = counter.incrementAndGet();
        this.results.put(requestID, Optional.empty());

        new Thread(() -> {
            try {
                Thread.sleep(5000L);  // artificial delay

                Template template = templateRepository.findById(id).orElseThrow(() -> new NotFoundException("Cannot find template"));
                String result = TemplateProcessor.render(template.getSource(), params);
                this.results.put(requestID, Optional.of(result));
            } catch (Throwable ex) {
                this.results.put(requestID, Optional.of(ex.toString()));
            }
        }).start();

        return json().put("request-id", requestID);
    }

    @GetMapping("/template/render-result/{id}")
    @ApiOperation(value = "Query results of async render")
    public JsonNode renderAsyncResult(@PathVariable("id") long rid) {
        Optional<String> value = this.results.getIfPresent(rid);
        if (value == null) {
            return json().put("status", "Not found or expired");
        }

        if (value.isPresent()) {
            return json().put("result", value.get()).put("status", "ok");
        } else {
            return json().put("status", "In progress");
        }
    }
}