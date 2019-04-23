package svc;

import caketpl.TemplateProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RestController
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

    // CRUD

    @PostMapping("/template/create")
    public String create(@RequestBody @Valid Template template) {
        template = templateRepository.save(template);
        return ""+template.getId();
    }

    // Rendering

    @PostMapping("/template/{id}/render")
    public String render(@PathVariable("id") long id, @RequestBody JsonNode params) throws Exception {
        Template template = templateRepository.findById(id).orElseThrow(() -> new NotFoundException("Cannot find template"));
        return TemplateProcessor.render(template.getSource(), params);
    }

    // Async render

    @PostMapping("/template/{id}/render/async")
    public String renderAsync(@PathVariable("id") long id, @RequestBody JsonNode params) {
        long requestID = counter.incrementAndGet();
        this.results.put(requestID, Optional.empty());

        new Thread(() -> {
            try {
                Thread.sleep(5000L);

                Template template = templateRepository.findById(id).orElseThrow(() -> new NotFoundException("Cannot find template"));
                String result = TemplateProcessor.render(template.getSource(), params);
                this.results.put(requestID, Optional.of(result));
            } catch (Throwable ex) {
                this.results.put(requestID, Optional.of(ex.toString()));
            }
        }).start();

        return ""+requestID;
    }

    @GetMapping("/template/render-result/{id}")
    public String renderAsyncResult(@PathVariable("id") long rid) {
        Optional<String> value = this.results.getIfPresent(rid);
        if (value == null) {
            return "Not found or expired";
        }

        if (value.isPresent()) {
            return value.get();
        } else {
            return "In progress";
        }
    }
}