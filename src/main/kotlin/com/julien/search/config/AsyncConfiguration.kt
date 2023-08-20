package com.julien.search.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
class AsyncConfiguration : AsyncConfigurer {

    @Value("\${async.queueCapacity:64}")
    var queueCapacity: Int = 64

    @Value("\${async.threadPoolSize:4}")
    var threadPoolSize: Int = 4

    @Override
    @Bean("threadPoolTaskExecutor")
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = threadPoolSize
        executor.maxPoolSize = queueCapacity
        executor.setQueueCapacity(queueCapacity)
        executor.setThreadNamePrefix("AsyncThread::")
        executor.initialize()
        return executor
    }
}
