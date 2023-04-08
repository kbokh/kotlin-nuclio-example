package com.bokhko.nuclio.handler.serdes

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class JsonSerializer : Serializer<JsonNode?> {
    private val objectMapper: ObjectMapper = ObjectMapper()
    override fun configure(config: Map<String?, *>?, isKey: Boolean) {
        //Nothing to Configure
    }

    override fun serialize(topic: String?, data: JsonNode?): ByteArray? {
        return if (data == null) {
            null
        } else try {
            objectMapper.writeValueAsBytes(data)
        } catch (e: JsonProcessingException) {
            throw SerializationException("Error serializing JSON message", e)
        }
    }

    override fun close() {
        //Not needed to implement
    }
}