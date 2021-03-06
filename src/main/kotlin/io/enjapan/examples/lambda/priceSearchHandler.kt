package io.enjapan.examples.lambda

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.amazonaws.services.pricing.AWSPricing
import com.amazonaws.services.pricing.AWSPricingClientBuilder
import com.amazonaws.services.pricing.model.DescribeServicesRequest
import com.amazonaws.services.pricing.model.GetProductsRequest
import com.amazonaws.services.pricing.model.Filter
import com.amazonaws.services.pricing.model.Service
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream
import java.io.OutputStream

/**
 * Requires the following permissions granted to the IAM user
 * - pricing:DescribeServices
 * - pricing:GetAttributeValues
 * - pricing:GetProducts
 */
class PriceSearch : RequestStreamHandler {
    companion object {
        const val ENDPOINT = "api.pricing.us-east-1.amazonaws.com"
        const val REGION = "us-east-1"
    }

    private val mapper = jacksonObjectMapper()

    data class Price(val serviceCode: String?, val priceList: MutableList<String>?)

    data class HandlerInput(val services: List<String>, val location: String)
    data class HandlerOutput(val result: List<Price>)

    private fun getAwsPricingClient(): AWSPricing {
        val endPointConfig = AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION)
        return AWSPricingClientBuilder.standard().withEndpointConfiguration(endPointConfig).build()
    }

    private fun findService(searchTerm: String): Service? {
        val client = getAwsPricingClient()
        val res = client.describeServices(DescribeServicesRequest())
        return res.services.find {
            it.serviceCode.equals(searchTerm, true) || it.serviceCode.contains(searchTerm, true)
        }
    }

    private fun genFilter(field: String, value: String, type: String = "TERM_MATCH"): Filter {
        return Filter().withType(type).withField(field).withValue(value)
    }

    private fun fetchPrices(serviceCode: String?, filter: Filter): MutableList<String>? {
        val client = getAwsPricingClient()
        val req = GetProductsRequest().withServiceCode(serviceCode).withFilters(filter)
        return client.getProducts(req).priceList
    }

    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val inputObj = mapper.readValue<HandlerInput>(input)
        val prices = inputObj.services.map {
            val serviceCode = findService(it)?.serviceCode
            val priceList = fetchPrices(serviceCode, genFilter("location", inputObj.location))
            Price(serviceCode, priceList)
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(output, HandlerOutput(prices))
    }
}
