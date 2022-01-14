package com.julien.search.model

data class YoutubeVideo(
    val id: String? = null,
    val title: String? = null,
    val filename: String? = null,
    val previouslyDownloaded: Boolean = false
) {
    var url: String? = null

    fun buildUrl(): String? {
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
