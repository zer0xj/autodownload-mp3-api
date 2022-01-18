package com.julien.search.controller

import com.julien.search.config.AsyncException
import com.julien.search.dao.DAOException
import com.julien.search.service.BaseException
import com.julien.search.service.ServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.IOException
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(value = ["com.julien.search.controller"])
class ControllerExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(BaseException::class)
    fun handleException(h: HttpServletRequest, b: BaseException): ResponseEntity<String> {
        val httpStatus: HttpStatus = b.errorCode?.httpStatus ?: when (b) {
            is AsyncException -> HttpStatus.INTERNAL_SERVER_ERROR
            is ControllerException -> HttpStatus.UNAUTHORIZED
            is DAOException -> HttpStatus.BAD_GATEWAY
            is ServiceException -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.SERVICE_UNAVAILABLE
        }
        val logMessage = "Caught ${b.javaClass.simpleName} @ ${b.getLogMessage()}, requestURI=${h.requestURI} - " +
                "returning a ${httpStatus.value()} (${httpStatus.reasonPhrase})"
        logger.error(logMessage)
        return ResponseEntity(b.toJson(), httpStatus)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(h: HttpServletRequest, ex: Exception): ResponseEntity<String> {
        val responseBody: String? = try {
            ex.message
        } catch (i: IOException) {
            logger.error("Could not write message to failure response", i)
            null
        }
        val httpStatus: HttpStatus = when (ex) {
            is AsyncException -> HttpStatus.INTERNAL_SERVER_ERROR
            is ControllerException -> HttpStatus.UNAUTHORIZED
            is DAOException -> HttpStatus.BAD_GATEWAY
            is ServiceException -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.SERVICE_UNAVAILABLE
        }
        val logMessage = "Caught ${ex.javaClass.simpleName}, requestURI=${h.requestURI} - ${ex.message}: returning a " +
                "${httpStatus.value()} (${httpStatus.reasonPhrase})"
        logger.error(logMessage, ex)
        return ResponseEntity(responseBody, httpStatus)
    }
}
