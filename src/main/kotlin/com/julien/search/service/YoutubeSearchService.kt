package com.julien.search.service

import com.julien.search.dao.HistoryDAO
import com.julien.search.dao.VideoDownloadDAO
import com.julien.search.dao.SearchDAO
import com.julien.search.model.YoutubeVideo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class YoutubeSearchService : SearchService {

    @Autowired
    private lateinit var historyDAO: HistoryDAO

    @Autowired
    private lateinit var searchDAO: SearchDAO

    @Autowired
    private lateinit var videoDownloadDAO: VideoDownloadDAO

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun search(query: String): List<YoutubeVideo> {

        logger.debug("search(query=$query)")
        val response: List<YoutubeVideo> = searchDAO.search(query)
        logger.debug("search(query=$query) RESPONSE: $response")

        return response
    }

    override fun searchAndDownload(query: String): YoutubeVideo? {

        logger.debug("searchAndDownload(query=$query)")
        val videoList: List<YoutubeVideo> = searchDAO.search(query)
        logger.debug("searchAndDownload(query=$query) SEARCH RESPONSE: $videoList")

        val result = downloadVideo(videoList)

        historyDAO.save(query, result)

        return result
    }

    private fun downloadVideo(videoList: List<YoutubeVideo>): YoutubeVideo? {
        for (video in videoList) {
            video.filename = videoDownloadDAO.download(video)
            if (video.filename != null) {
                return video
            }
        }
        return null
    }
}
