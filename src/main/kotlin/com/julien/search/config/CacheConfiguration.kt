package com.julien.search.config

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.julien.search.model.Mp3DownloadResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfiguration {

    @Value("\${cache.processedVideos.minutesAfterAccessTTL:10}")
    private var processedVideosCacheMinutesAfterAccessTTL: Long = 10

    @Value("\${cache.processedVideos.minutesAfterWriteTTL:30}")
    private var processedVideosCacheMinutesAfterWriteTTL: Long = 30

    @Bean
    fun processedVideosCache(): LoadingCache<Int, MutableMap<String, Mp3DownloadResponse>> = CacheBuilder.newBuilder()
        .maximumSize(2048)
        .expireAfterAccess(processedVideosCacheMinutesAfterAccessTTL, TimeUnit.MINUTES)
        .expireAfterWrite(processedVideosCacheMinutesAfterWriteTTL, TimeUnit.MINUTES)
        .build(object : CacheLoader<Int, MutableMap<String, Mp3DownloadResponse>>() {
            @Throws(Exception::class)
            override fun load(userId: Int): MutableMap<String, Mp3DownloadResponse> = mutableMapOf()
        })
}
