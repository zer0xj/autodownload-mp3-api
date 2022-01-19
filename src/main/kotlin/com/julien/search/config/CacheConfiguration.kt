package com.julien.search.config

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.julien.search.model.ProcessingJob
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfiguration {

    @Value("\${cache.processedVideos.minutesAfterAccessTTL:15}")
    private var processedVideosCacheMinutesAfterAccessTTL: Long = 15

    @Value("\${cache.processedVideos.minutesAfterWriteTTL:45}")
    private var processedVideosCacheMinutesAfterWriteTTL: Long = 45

    @Bean
    fun processedVideos(): Cache<String, ProcessingJob> = CacheBuilder.newBuilder()
        .maximumSize(2048)
        .expireAfterAccess(processedVideosCacheMinutesAfterAccessTTL, TimeUnit.MINUTES)
        .expireAfterWrite(processedVideosCacheMinutesAfterWriteTTL, TimeUnit.MINUTES)
        .build()
}
