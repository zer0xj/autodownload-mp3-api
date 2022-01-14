package com.julien.search.model

import org.springframework.http.HttpStatus


enum class ErrorCode(val httpStatus: HttpStatus) {
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST),
    INVALID_YOUTUBE_RESPONSE_DATA(HttpStatus.BAD_GATEWAY),
    MISSING_CONFIGURATION(HttpStatus.INTERNAL_SERVER_ERROR),
    YOUTUBE_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE)
}