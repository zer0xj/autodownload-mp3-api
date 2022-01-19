package com.julien.search.service

import com.google.common.cache.Cache
import com.julien.search.dao.HistoryDAO
import com.julien.search.dao.SearchDAO
import com.julien.search.dao.UserDAO
import com.julien.search.dao.VideoDownloadDAO
import com.julien.search.model.Mp3DownloadResponse
import com.julien.search.model.ProcessingJob
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

    @Autowired
    private lateinit var processedVideos: Cache<String, ProcessingJob>

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getJobStatus(userId: Int, jobId: String): Mp3DownloadResponse? {

        logger.debug("getJobStatus(userId=$userId, jobId=$jobId)")

        val user = userDAO.validateUserName(userId)

        val filteredJobs: List<ProcessingJob> = processedVideos.asMap().values.filter { it.jobId == jobId }

        if (filteredJobs.isEmpty()) {
            return null
        } else if (filteredJobs.size > 1) {
            val logMessage = "Found more than one job in cache for jobId[$jobId]: $filteredJobs. Returning first " +
                    "value (if applicable) for user"
            if (logger.isDebugEnabled) {
                logger.warn("${logMessage}[$user].")
            } else {
                logger.warn("${logMessage}Id[$userId].")
            }
        }

        val response = if (user.adminUser) {
            filteredJobs.firstOrNull()?.response
        } else {
            filteredJobs.firstOrNull { it.userId == userId }?.response
        }

        logger.debug("getJobStatus(userId=$userId, jobId=$jobId) RESPONSE: $response")

        return response
    }

    override fun getProcessingJobs(userId: Int): List<Mp3DownloadResponse> {

        logger.debug("getProcessingJobs(userId=$userId)")

        val user = userDAO.validateUserName(userId)

        val response = if (user.adminUser) {
            processedVideos.asMap().values.mapNotNull { it.response }.toList()
        } else {
            processedVideos.asMap().values.filter { it.userId == userId }.mapNotNull { it.response }.toList()
        }

        logger.debug("getProcessingJobs(userId=$userId) RESPONSE: $response")

        return response
    }

    override fun search(userId: Int, query: String): List<YoutubeVideo> {

        logger.debug("search(userId=$userId, query=$query)")

        userDAO.validateUserName(userId)

        val response: List<YoutubeVideo> = searchDAO.search(query)

        logger.debug("search(userId=$userId, query=$query) RESPONSE: $response")

        return response
    }

    override fun searchAndDownload(userId: Int, query: String): ProcessingJob {

        userDAO.validateUserName(userId)

        val existingJob: ProcessingJob? = processedVideos.getIfPresent(query)

        return if (existingJob == null) {

            val uuid = UUID.randomUUID().toString()

            processedVideos.put(query,
                ProcessingJob(
                    userId = userId,
                    jobId = uuid,
                    response = Mp3DownloadResponse(query = query)
                ))

            CompletableFuture.runAsync { asyncSearchAndDownload(userId, uuid, query) }

            ProcessingJob(jobId = uuid)
        } else {
            ProcessingJob(
                jobId = existingJob.jobId,
                userId = if (existingJob.userId != userId) {
                    existingJob.userId
                } else {
                    null
                })
        }
    }

    @Async("threadPoolTaskExecutor")
    fun asyncSearchAndDownload(userId: Int, jobId: String, query: String) {
        logger.debug("asyncSearchAndDownload(userId=$userId, jobId=$jobId, query=$query)")

        val videoList: List<YoutubeVideo> = searchDAO.search(query)

        logger.debug("asyncSearchAndDownload(userId=$userId, jobId=$jobId, query=$query) SEARCH RESPONSE: $videoList")

        val result: YoutubeVideo? = downloadVideo(videoList)

        logger.debug("asyncSearchAndDownload(userId=$userId, jobId=$jobId, query=$query) DOWNLOAD RESPONSE: $result")

        processedVideos.put(query,
            ProcessingJob(
                userId = userId,
                jobId = jobId,
                response = Mp3DownloadResponse.ModelMapper.from(result, query)
            ))

        historyDAO.save(query, result)
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
}
