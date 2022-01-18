package com.julien.search.config

import com.julien.search.model.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

// TODO either remove this, or put it into use
class DefaultAsyncExceptionHandler : AsyncUncaughtExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @Throws(AsyncException::class)
    override fun handleUncaughtException(throwable: Throwable, method: Method, vararg objects: Any?) {
        logger.error("Caught ${throwable.javaClass.simpleName} when calling " +
                "${method.declaringClass.simpleName}:${method.name} with arguments[$objects]", throwable)
        throw AsyncException(throwable.message, throwable, "${method.name}($objects)", ErrorCode.ASYNC_ERROR)
    }
}
