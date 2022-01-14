package com.julien.search.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Mp3DownloadResponse(
    val filename: String? = null,
    val message: String? = null,
    val query: String? = null,
    val url: String? = null
) {

    object ModelMapper {
        fun from(youtubeVideo: YoutubeVideo?, query: String) =
            Mp3DownloadResponse(
                filename = youtubeVideo?.filename,
                message = if (youtubeVideo != null) {
                    if (youtubeVideo.previouslyDownloaded) {
                        "Previously"
                    } else {
                        "Successfully"
                    } + " downloaded ${youtubeVideo.url} for search query[$query]" +
                            if (youtubeVideo.filename != null) {
                                " as \"${youtubeVideo.filename}\""
                            } else {
                                ""
                            }
                } else {
                    "Failed to download an MP3 for search query[$query]"
                },
                query = query,
                url = youtubeVideo?.url
            )
    }
}

