package caketpl

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException
import java.text.ParseException


@RunWith(SpringRunner::class)
class JsonValueFormatterTest {

    @Test
    @Throws(IOException::class, ParseException::class)
    fun test1() {
        val jsonString = "{`k1`:`v1`,`k2`:`v2`}".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            jsonString,
            JsonValueFormatter.format(node)
        )
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun test2() {
        val jsonString = "[1, 2, 3]".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            "1 2 3",
            JsonValueFormatter.format(node)
        )
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun test3() {
        val jsonString = "{`value`: 123}".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            "123",
            JsonValueFormatter.format(node.get("value"))
        )
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun test4() {
        val jsonString = "{`value`: `whatever`}".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            "whatever",
            JsonValueFormatter.format(node.get("value"))
        )
    }

}