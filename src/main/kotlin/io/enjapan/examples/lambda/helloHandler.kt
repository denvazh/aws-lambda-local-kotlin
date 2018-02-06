package io.enjapan.examples.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import java.io.*
import com.fasterxml.jackson.module.kotlin.*

class Hello : RequestStreamHandler {
    data class HandlerInput(val name: String)
    data class HandlerOutput(val message: String)

    private val mapper = jacksonObjectMapper()

    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val inputObj = mapper.readValue<HandlerInput>(input)
        mapper.writeValue(output, HandlerOutput("Hello ${inputObj.name}"))
    }
}
