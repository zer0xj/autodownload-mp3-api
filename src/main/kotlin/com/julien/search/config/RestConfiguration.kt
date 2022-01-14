package com.julien.search.config

import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfiguration {

    @Bean
    fun restTemplate(
            @Value("\${http.pool.poolSize:50}") poolSize: Int,
            @Value("\${http.pool.getConnectionTimeout:5000}") getConnectionTimeout: Int,
            @Value("\${http.pool.readTimeout:100000}") readTimeout: Int): RestTemplate {

        val connectionManager = PoolingHttpClientConnectionManager()
        connectionManager.setMaxTotal(poolSize)
        connectionManager.setDefaultMaxPerRoute(20)

        val requestConfig: RequestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(getConnectionTimeout) // timeout to get connection from pool
                .setSocketTimeout(readTimeout) // standard connection timeout
                .setConnectTimeout(getConnectionTimeout) // standard connection timeout
                .build()

        val httpClient: HttpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig).build()

        val requestFactory: ClientHttpRequestFactory = HttpComponentsClientHttpRequestFactory(httpClient)

        return RestTemplate(requestFactory)
    }
}
