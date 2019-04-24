package caketpl

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

// JsonValueFormatter outputs JSON values as required by the spec: 1 -> "1", [1,2,3] -> "1 2 3", otherwise as is.
object JsonValueFormatter {
    @Throws(JsonProcessingException::class)
    fun format(value: JsonNode): String {
        // If that's an array, output its contents separated by space
        if (value.isArray) {
            val ret = ArrayList<String>()
            for (i in 0 until value.size()) {
                ret.add(JsonValueFormatter.format(value.get(i)))
            }
            return ret.joinToString(" ")
        }

        // If that's a primitive type, output as is
        if (value.isNumber || value.isTextual) {
            return value.asText()
        }

        // Otherwise, output as JSON
        return ObjectMapper().writeValueAsString(value)
    }
}
