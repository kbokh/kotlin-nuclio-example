package com.bokhko.nuclio.handler.mappers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

class EventMapper(value: String, headers: Map<String, Any>) {

    val eventHeader: MutableMap<String, String> = HashMap()
    val eventBodyAsJsonNode: JsonNode = jacksonObjectMapper().readTree(value)

    init {
        val decoder: Base64.Decoder = Base64.getDecoder()
        for (item in headers) {
            eventHeader[item.key] = String(decoder.decode(item.value.toString()))
        }
    }
}