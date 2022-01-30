package com.julien.search.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.concurrent.CompletableFuture

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProcessingJob(
    val jobId: String,
    val response: Mp3DownloadResponse? = null,
    val userId: Int? = null,
    @JsonIgnore
    private val future: CompletableFuture<Void>? = null
) {
    fun cancel() {
        response?.cancelled?.set(true)
        response?.youtubeDL?.cancel()
        future?.cancel(true)
    }

    fun copy(newResponse: Mp3DownloadResponse?): ProcessingJob = ProcessingJob(jobId, newResponse, userId, future)

    fun isCancelled(): Boolean = response?.cancelled?.get() ?: false
}
