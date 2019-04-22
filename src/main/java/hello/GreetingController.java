package hello;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private TemplateRepository templateRepository;

    public GreetingController() {
    }

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @PostMapping("/greeting")
    public String greetingPost(@RequestBody RenderRequest rr) {
        Template t = templateRepository.findById(1L).get();
        return "Result: "+TemplateProcessor.process(t.getSource(), rr.getParams());
    }
}