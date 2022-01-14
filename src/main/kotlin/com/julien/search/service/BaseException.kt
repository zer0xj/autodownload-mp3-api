package com.julien.search.service

import com.julien.search.model.ErrorCode


open class BaseException : RuntimeException {

    val errorCode: ErrorCode?
    val sourceId: String?

    constructor(sourceId: String?) : super() {
        this.errorCode = null
        this.sourceId = sourceId
    }

    constructor(message: String?, cause: Throwable?, sourceId: String?, errorCode: ErrorCode?) : super(message, cause) {
        this.errorCode = errorCode
        this.sourceId = sourceId
    }

    constructor(message: String?, sourceId: String?, errorCode: ErrorCode?) : super(message) {
        this.errorCode = errorCode
        this.sourceId = sourceId
    }

    constructor(message: String?, cause: Throwable?, sourceId: String?) : super(message, cause) {
        this.errorCode = null
        this.sourceId = sourceId
    }

    constructor(message: String?, sourceId: String?) : super(message) {
        this.errorCode = null
        this.sourceId = sourceId
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        this.errorCode = null
        this.sourceId = null
    }

    constructor(cause: Throwable?, sourceId: String?, errorCode: ErrorCode?) : super(cause?.message, cause) {
        this.errorCode = errorCode
        this.sourceId = sourceId
    }

    constructor(cause: Throwable?, sourceId: String?) : super(cause) {
        this.errorCode = null
        this.sourceId = sourceId
    }

    companion object {
        private const val serialVersionUID = 1L
    }

    open fun getLogMessage(): String {
        val stringBuilder = StringBuilder()
        if (sourceId != null) {
            stringBuilder.append("$sourceId: ")
        }
        if (message != null) {
            stringBuilder.append("$message ")
        }
        if (errorCode != null) {
            stringBuilder.append("(${errorCode.name})")
        }
        return stringBuilder.trim().toString()
    }

    open fun toJson(): String {
        val stringBuilder = StringBuilder()
        if (sourceId != null) {
            stringBuilder.append("\"sourceId\": \"$sourceId\"")
        }
        if (message != null) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append(", ")
            }
            stringBuilder.append("\"message\": \"$message\"")
        }
        if (errorCode != null) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append(", ")
            }
            stringBuilder.append("\"errorCode\": \"${errorCode.name}\"")
        }
        return "{$stringBuilder}"
    }
}
