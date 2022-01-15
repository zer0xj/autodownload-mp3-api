package com.julien.search.dao

import com.google.api.client.http.HttpRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import com.julien.search.model.*
import org.apache.commons.lang3.StringEscapeUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class YoutubeSearchDAO : SearchDAO {

    @Value("\${youtube.maxResults:10}")
    private var maxSearchResults: Long = 10

    @Value("\${youtube.api-key}")
    private lateinit var youtubeApiKey: String

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun search(query: String): List<YoutubeVideo> {

        try {
            val youtube: YouTube = getYouTube()

            // Define what info we want to get
            val search = youtube.search().list("id,snippet")

            search.key = youtubeApiKey

            // Set the search term
            search.q = query

            // We only want video results
            search.type = "video"

            // Set the fields that we're going to use
            search.fields = "items(id/kind,id/videoId,snippet/title)"

            search.maxResults = maxSearchResults

            // Perform the search and parse the results
            val searchResponse = search.execute()
            val searchResultList: List<SearchResult> = searchResponse.items ?: emptyList()

            return searchResultList.map {
                YoutubeVideo(
                    id = it.id.videoId,
                    title = StringEscapeUtils.unescapeHtml4(it.snippet.title).replace(Regex("[\\\\/:*?<>|]"), "")
                )
            }.toList()
        } catch (e: Exception) {
            logger.error("Caught ${e.javaClass.simpleName} trying to search for \"$query\":", e)
        }
        return emptyList()
    }

    private fun getYouTube(): YouTube {
        return YouTube.Builder(NetHttpTransport(), GsonFactory()) {}.setApplicationName("autodownload-mp3-api").build()
    }
}
