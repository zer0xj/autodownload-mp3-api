package com.julien.search.model

import com.julien.search.dao.LocalYoutubeDL

data class YoutubeVideo(
    val id: String? = null,
    val filename: String? = null,
    val previouslyDownloaded: Boolean = false,
    val title: String? = null,
    val youtubeDL: LocalYoutubeDL? = null
) {
    var url: String? = null

    private fun buildUrl(): String? {
        return if (id != null) {
            val builder = StringBuilder()
            builder.append("https://www.youtube.com/watch?v=")
            builder.append(this.id)
            builder.toString()
        } else {
            null
        }
    }

    init {
        url = buildUrl()
    }
}
