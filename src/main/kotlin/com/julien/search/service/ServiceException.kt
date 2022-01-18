package com.julien.search.service

import com.julien.search.model.ErrorCode

class ServiceException : BaseException {
    constructor(sourceId: String?) : super(sourceId)
    constructor(message: String?, cause: Throwable?, sourceId: String?, errorCode: ErrorCode?) : super(message, cause, sourceId, errorCode)
    constructor(message: String?, sourceId: String?, errorCode: ErrorCode?) : super(message, sourceId, errorCode)
    constructor(message: String?, cause: Throwable?, sourceId: String?) : super(message, cause, sourceId)
    constructor(message: String?, sourceId: String?) : super(message, sourceId)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?, sourceId: String?, errorCode: ErrorCode?) : super(cause, sourceId, errorCode)
    constructor(cause: Throwable?, sourceId: String?) : super(cause, sourceId)
}
