package com.julien.search.controller.v1

import com.julien.search.model.ErrorCode
import com.julien.search.service.BaseException


open class ControllerException(message: String?, sourceId: String?, errorCode: ErrorCode?) :
        BaseException(message, sourceId, errorCode)
