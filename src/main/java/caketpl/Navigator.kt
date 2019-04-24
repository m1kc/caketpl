package caketpl

import com.fasterxml.jackson.databind.JsonNode

import java.text.ParseException
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

class Navigator(var current: JsonNode) {

    fun goKey(key: String): Navigator {
        current = current.get(key)
        return this
    }

    fun goIndex(index: Int): Navigator {
        current = current.get(index)
        return this
    }

    @Throws(ParseException::class)
    fun goTemplate(s: String): Navigator {
        val elements = parseTemplate(s)
        for (el in elements) {
            el.executeOn(this)
        }
        return this
    }

    companion object {

        @Throws(ParseException::class)
        fun parseTemplate(s: String): Iterable<NavigatorElement> {
            val patternKey = Pattern.compile("^([a-zA-Z0-9_]+)([.]?)")
            val patternIndex = Pattern.compile("^\\[([0-9]+)\\]([.]?)")
            var m: Matcher
            var s = s

            val ret = ArrayList<NavigatorElement>()

            while (true) {
                if (s.isEmpty()) {
                    return ret
                }

                // index navigation?
                m = patternIndex.matcher(s)
                if (m.find()) {
                    ret.add(NavigatorElement(Integer.parseInt(m.group(1))))
                    s = s.substring(m.end())
                    continue
                }

                // key navigation?
                m = patternKey.matcher(s)
                if (m.find()) {
                    ret.add(NavigatorElement(m.group(1)))
                    s = s.substring(m.end())
                    continue
                }

                // I dunno, invalid
                throw ParseException("Could not parse template near: `$s`", 0)
            }
        }
    }
}
