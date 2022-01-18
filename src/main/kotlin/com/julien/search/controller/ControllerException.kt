package com.julien.search.controller

import com.julien.search.model.ErrorCode
import com.julien.search.service.BaseException

class ControllerException(message: String?, sourceId: String?, errorCode: ErrorCode?) :
        BaseException(message, sourceId, errorCode)
