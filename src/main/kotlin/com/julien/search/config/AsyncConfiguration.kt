package com.julien.search.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
class AsyncConfiguration {

    @Bean("threadPoolTaskExecutor")
    fun asyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.setQueueCapacity(50)
        executor.setThreadNamePrefix("AsyncThread::")
        executor.initialize()
        return executor
    }
}
