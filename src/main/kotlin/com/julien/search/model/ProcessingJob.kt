package com.julien.search.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProcessingJob(
    val jobId: String,
    val response: Mp3DownloadResponse? = null,
    val userId: Int? = null
)
