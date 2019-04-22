import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.Navigator;
import hello.TemplateProcessor;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class NavigatorTest {

    @Test
    public void testNavigator1() throws IOException, ParseException {
        String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals("v1", new Navigator(node).goKey("k1").getCurrent().asText());
    }

    @Test
    public void testNavigator2() throws IOException, ParseException {
        String jsonString = "[\"ok\", \"not ok\"]";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals("ok", new Navigator(node).goIndex(0).getCurrent().asText());
    }

    @Test
    public void testNavigator3() throws IOException, ParseException {
        String jsonString = "[1,2,3]";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals("1", new Navigator(node).goIndex(0).getCurrent().asText());
    }

    @Test
    public void testNavigator4() throws IOException, ParseException {
        String jsonString = "{`cities`: [{`name`: `Penza`}]}".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                "Penza",
                new Navigator(node)
                .goTemplate("cities[0].name")
                .getCurrent()
                .asText()
        );
    }


    @Test
    public void testNavigator5() throws IOException, ParseException {
        String jsonString = "[[[123]]]".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                "123",
                new Navigator(node)
                        .goTemplate("[0][0][0]")
                        .getCurrent()
                        .asText()
        );
    }

    @Test
    public void testNavigator6() throws IOException, ParseException {
        String jsonString = "[[[{ `city`: `Penza` }]]]".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                "Penza",
                new Navigator(node)
                        .goTemplate("[0][0][0].city")
                        .getCurrent()
                        .asText()
        );
    }

    @Test
    public void testNavigator7() throws IOException, ParseException {
        String jsonString = "{`city`: {`very`: {`good`: `yes`}}}".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                "yes",
                new Navigator(node)
                        .goTemplate("city.very.good")
                        .getCurrent()
                        .asText()
        );
    }

}