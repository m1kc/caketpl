package caketpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class TemplateProcessorTest {

    @Test
    public void smokeTest() throws IOException, ParseException {
        String jsonString = "{`k1`:`v1`,`k2`:`v2`, `k3`:[1,2,3]}".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals("v1 is better than v2", TemplateProcessor.render("${k1} is better than ${k2}", node));
        Assert.assertEquals("1 2 3 clap clap", TemplateProcessor.render("${k3} clap clap", node));
    }

}