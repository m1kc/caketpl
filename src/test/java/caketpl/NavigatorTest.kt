package caketpl

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException
import java.text.ParseException


@RunWith(SpringRunner::class)
class NavigatorTest {

    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator1() {
        val jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}"
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals("v1", Navigator(node).goKey("k1").current.asText())
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator2() {
        val jsonString = "[\"ok\", \"not ok\"]"
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals("ok", Navigator(node).goIndex(0).current.asText())
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator3() {
        val jsonString = "[1,2,3]"
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals("1", Navigator(node).goIndex(0).current.asText())
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator4() {
        val jsonString = "{`cities`: [{`name`: `Penza`}]}".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            "Penza",
            Navigator(node)
                .goTemplate("cities[0].name")
                .current
                .asText()
        )
    }


    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator5() {
        val jsonString = "[[[123]]]".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            "123",
            Navigator(node)
                .goTemplate("[0][0][0]")
                .current
                .asText()
        )
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator6() {
        val jsonString = "[[[{ `city`: `Penza` }]]]".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            "Penza",
            Navigator(node)
                .goTemplate("[0][0][0].city")
                .current
                .asText()
        )
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator7() {
        val jsonString = "{`city`: {`very`: {`good`: `yes`}}}".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        Assert.assertEquals(
            "yes",
            Navigator(node)
                .goTemplate("city.very.good")
                .current
                .asText()
        )
    }

    @Test
    @Throws(IOException::class, ParseException::class)
    fun testNavigator8() {
        val jsonString = "{`city`: {`very`: {`good`: `yes`}}}".replace('`', '"')
        val node = ObjectMapper().readTree(jsonString)

        try {
            Navigator(node).goTemplate("FD(FS(F*F(NFD(*(*($#&@!(&(#@")
            throw AssertionError("Expected to throw")
        } catch (ex: Exception) {
            return
        }
    }

}