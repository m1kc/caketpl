package caketpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import svc.Template;


@RunWith(SpringRunner.class)
public class TemplateProcessorTest {

    @Test
    public void smokeTest() throws IOException, ParseException {
        String jsonString = "{`k1`:`v1`,`k2`:`v2`, `k3`:[1,2,3], `city`: {`very`: {`good`: `yes`}}}".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals("v1 is better than v2", TemplateProcessor.render("${k1} is better than ${k2}", node));
        Assert.assertEquals("1 2 3 clap clap", TemplateProcessor.render("${k3} clap clap", node));
        Assert.assertEquals("Is this city any good? yes", TemplateProcessor.render("Is this city any good? ${city.very.good}", node));
    }

    @Test
    public void testSelectorSearch() throws IOException, ParseException {
        Assert.assertArrayEquals(
                new String[]{"k1", "k2"},
                TemplateProcessor.findSelectors("${k1} is better than ${k2}")
        );
        Assert.assertArrayEquals(
                new String[]{"k3"},
                TemplateProcessor.findSelectors("${k3} clap clap")
        );
        Assert.assertArrayEquals(
                new String[]{"actions[0]", "actions[1]", "car", "entity.name"},
                TemplateProcessor.findSelectors("So I heard you like ${car}, so I put ${car} in your ${entity.name} so you can ${actions[0]} while you ${actions[1]}")
        );
    }

    @Test
    public void validationTest() throws IOException, ParseException {
        Assert.assertEquals(true, TemplateProcessor.isValid("hello world"));
        Assert.assertEquals(true, TemplateProcessor.isValid("hello ${name}!"));
        Assert.assertEquals(true, TemplateProcessor.isValid("So I heard you like ${car}, so I put ${car} in your ${entity.name} so you can ${actions[0]} while you ${actions[1]}"));
        Assert.assertEquals(false, TemplateProcessor.isValid("So ${FJIJSDFIJFSDISJDFI&#$(*@#(&%#(@&#}, wow"));
    }

}