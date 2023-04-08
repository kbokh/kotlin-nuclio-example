package com.bokhko.nuclio.handler.clients

import com.bokhko.nuclio.handler.serdes.JsonSerializer
import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.internals.RecordHeaders
import org.slf4j.LoggerFactory
import java.util.*

class KafkaClient {

    private val outputTopic: String = System.getenv("OUTPUT_TOPIC") ?: "output-topic"
    private val producer: KafkaProducer<JsonNode, JsonNode>
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("BOOTSTRAP_SERVERS") ?: "redpanda:29092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.qualifiedName
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.qualifiedName

        producer = KafkaProducer(props)
    }

    fun sendOutputRecord(key: JsonNode?, value: JsonNode, headersMap: Map<String, String>) {

        logger.info("Calling sendOutputRecord")

        val headers = RecordHeaders()

        for (item in headersMap) {
            headers.add(item.key, item.value.toByteArray())
        }

        producer.send(
            ProducerRecord<JsonNode, JsonNode>(
                outputTopic, null, null, value, headers
            )
        ) { m: RecordMetadata, e: Exception? ->
            when (e) {
                // no exception, good to go!
                null -> logger.info("Produced record to topic ${m.topic()} partition [${m.partition()}] @ offset ${m.offset()}")
                // print stacktrace in case of exception
                else -> logger.info("Error sending record to topic ${m.topic()}", "error", e.printStackTrace())
            }
        }
    }
}