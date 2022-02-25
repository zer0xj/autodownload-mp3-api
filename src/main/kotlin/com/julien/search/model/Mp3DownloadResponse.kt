package com.julien.search.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.julien.search.dao.LocalYoutubeDL
import java.util.concurrent.atomic.AtomicBoolean

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Mp3DownloadResponse(
    @JsonIgnore
    val cancelled: AtomicBoolean = AtomicBoolean(false),
    val filename: String? = null,
    val previouslyDownloaded: Boolean? = null,
    val query: String? = null,
    @JsonIgnore
    val success: Boolean? = null,
    val url: String? = null,
    @JsonIgnore
    val youtubeDL: LocalYoutubeDL? = null
) {

    @JsonProperty("progress")
    fun getProgress() = youtubeDL?.getProgress()

    @JsonProperty("status")
    fun getStatus(): String =
        when (this.success) {
            true -> {
                if (this.cancelled.get()) {
                    Constants.STATUS_CANCELLED
                } else if (this.getProgress() != null) {
                    Constants.STATUS_PENDING
                } else {
                    Constants.STATUS_COMPLETE
                }
            }
            false -> {
                Constants.STATUS_FAILURE
            }
            else -> {
                Constants.STATUS_PROCESSING
            }
        }

    object ModelMapper {
        fun from(youtubeVideo: YoutubeVideo?, query: String, youtubeDL: LocalYoutubeDL? = null) =
            Mp3DownloadResponse(
                filename = youtubeVideo?.filename,
                previouslyDownloaded = youtubeVideo?.previouslyDownloaded,
                query = query,
                success = (youtubeVideo != null),
                url = youtubeVideo?.url,
                youtubeDL = youtubeDL
            )
    }
}
