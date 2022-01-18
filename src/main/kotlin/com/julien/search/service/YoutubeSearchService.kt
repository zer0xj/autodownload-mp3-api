package com.julien.search.service

import com.julien.search.dao.HistoryDAO
import com.julien.search.dao.VideoDownloadDAO
import com.julien.search.dao.SearchDAO
import com.julien.search.dao.UserDAO
import com.julien.search.model.JobResponse
import com.julien.search.model.Mp3DownloadResponse
import com.julien.search.model.YoutubeVideo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class YoutubeSearchService : SearchService {

    @Autowired
    private lateinit var historyDAO: HistoryDAO

    @Autowired
    private lateinit var searchDAO: SearchDAO

    @Autowired
    private lateinit var userDAO: UserDAO

    @Autowired
    private lateinit var videoDownloadDAO: VideoDownloadDAO

    private val processedVideos: MutableMap<String, Mp3DownloadResponse> = mutableMapOf() // TODO convert to Guava cache

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getDownloadStatus(userId: Int, jobId: String): Mp3DownloadResponse? {

        logger.debug("getDownloadStatus(userId=$userId, jobId=$jobId)")

        userDAO.validateUserName(userId)

        return processedVideos[jobId]
    }

    override fun search(userId: Int, query: String): List<YoutubeVideo> {

        logger.debug("search(userId=$userId, query=$query)")

        userDAO.validateUserName(userId)

        val response: List<YoutubeVideo> = searchDAO.search(query)

        logger.debug("search(userId=$userId, query=$query) RESPONSE: $response")

        return response
    }

    override fun searchAndDownload(userId: Int, query: String): JobResponse {

        val uuid = UUID.randomUUID().toString()

        CompletableFuture.runAsync { asyncSearchAndDownload(userId, uuid, query) }

        return JobResponse(jobId = uuid)
    }

    private fun downloadVideo(videoList: List<YoutubeVideo>): YoutubeVideo? {
        for (video in videoList) {
            val downloadedVideo = videoDownloadDAO.download(video)
            if (downloadedVideo?.filename != null) {
                return downloadedVideo
            }
        }
        return null
    }

    @Async("threadPoolTaskExecutor")
    fun asyncSearchAndDownload(userId: Int, jobId: String, query: String) {
        logger.debug("asyncSearchAndDownload(userId=$userId, jobId=$jobId, query=$query)")

        userDAO.validateUserName(userId)

        processedVideos[jobId] = Mp3DownloadResponse(query = query)

        val videoList: List<YoutubeVideo> = searchDAO.search(query)

        logger.debug("asyncSearchAndDownload(userId=$userId, jobId=$jobId, query=$query) SEARCH RESPONSE: $videoList")

        val result: YoutubeVideo? = downloadVideo(videoList)

        logger.debug("asyncSearchAndDownload(userId=$userId, jobId=$jobId, query=$query) DOWNLOAD RESPONSE: $result")

        processedVideos[jobId] = Mp3DownloadResponse.ModelMapper.from(result, query)

        historyDAO.save(query, result)
    }
}
