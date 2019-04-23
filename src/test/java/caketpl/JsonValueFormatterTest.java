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
public class JsonValueFormatterTest {

    @Test
    public void test1() throws IOException, ParseException {
        String jsonString = "{`k1`:`v1`,`k2`:`v2`}".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                jsonString,
                JsonValueFormatter.format(node)
        );
    }

    @Test
    public void test2() throws IOException, ParseException {
        String jsonString = "[1, 2, 3]".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                "1 2 3",
                JsonValueFormatter.format(node)
        );
    }

    @Test
    public void test3() throws IOException, ParseException {
        String jsonString = "{`value`: 123}".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                "123",
                JsonValueFormatter.format(node.get("value"))
        );
    }

    @Test
    public void test4() throws IOException, ParseException {
        String jsonString = "{`value`: `whatever`}".replace('`', '"');

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonString);

        Assert.assertEquals(
                "whatever",
                JsonValueFormatter.format(node.get("value"))
        );
    }

}