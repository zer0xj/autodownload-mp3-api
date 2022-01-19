package com.julien.search.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Mp3DownloadResponse(
    val filename: String? = null,
    val previouslyDownloaded: Boolean? = null,
    val query: String? = null,
    val success: Boolean? = null,
    val url: String? = null
) {
    @JsonProperty("message")
    fun getMessage(): String {
        return if (this.success == true) {
            if (this.previouslyDownloaded == true) {
                "Previously"
            } else {
                "Successfully"
            } + " downloaded ${this.url} for search query[$query]" +
                    if (this.filename != null) {
                        " as \"${this.filename}\""
                    } else {
                        ""
                    }
        } else if (this.success == false) {
            "Failed to download an MP3 for search query[$query]"
        } else {
            "Currently processing search query[$query]"
        }
    }

    object ModelMapper {
        fun from(youtubeVideo: YoutubeVideo?, query: String) =
            Mp3DownloadResponse(
                filename = youtubeVideo?.filename,
                previouslyDownloaded = youtubeVideo?.previouslyDownloaded,
                query = query,
                success = (youtubeVideo != null),
                url = youtubeVideo?.url
            )
    }
}
