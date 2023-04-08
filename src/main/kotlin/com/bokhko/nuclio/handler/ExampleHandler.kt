package com.bokhko.nuclio.handler

import com.bokhko.nuclio.handler.clients.KafkaClient
import com.bokhko.nuclio.handler.mappers.EventMapper


import io.nuclio.*


class ExampleHandler : EventHandler {

    private val kafkaClient: KafkaClient = KafkaClient()


    override fun handleEvent(context: Context, event: Event): Response? {

        val logger = context.logger
        logger.debugWith("Got trigger:", "kind", event.triggerInfo.kindName)
        val response = when (event.triggerInfo.kindName) {
            "kafka-cluster" -> processKafkaEvents(event, logger)
            "http" -> processHttpEvents(event, logger)
            else -> kotlin.run {
                logger.infoWith("Got unknown trigger:", "kind ", event.triggerInfo.kindName)
                Response().setBody("Got unknown trigger").setStatusCode(500)
            }

        }
        return response
    }

    private fun processKafkaEvents(event: Event, logger: Logger): Response? {
        val eventMapper = EventMapper(String(event.body, Charsets.UTF_8), event.headers)

        logger.debugWith("Got headers:", "headers", eventMapper.eventHeader.toString())

        logger.infoWith("Got value:", "value", event.body.toString())

        kafkaClient.sendOutputRecord(null, eventMapper.eventBodyAsJsonNode, eventMapper.eventHeader)

        return Response().setBody("Kafka record processed successfully")
    }

    private fun processHttpEvents(event: Event, logger: Logger): Response? {
        logger.infoWith("Got http trigger", "Not implemented yet", event.body.toString())
        return Response().setBody("The HTTP trigger is not implemented yet. Skipped")
    }

}