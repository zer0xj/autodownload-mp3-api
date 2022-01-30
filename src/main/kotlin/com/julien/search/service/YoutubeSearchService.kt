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

    override fun cancelJob(userId: Int, jobId: String): Mp3DownloadResponse? {

        logger.debug("cancelJob(userId=$userId, jobId=$jobId)")

        val job = getProcessingJob(userId, jobId)

        logger.debug("Cancelling download job[$job] for jobId[$jobId]")
        job?.cancel()

        logger.debug("cancelJob(userId=$userId, jobId=$jobId) RESPONSE: ${job?.response}")

        return job?.response
    }

    override fun getJobStatus(userId: Int, jobId: String): Mp3DownloadResponse? = getProcessingJob(userId, jobId)?.response

    override fun getJobStatuses(userId: Int): Map<String, Mp3DownloadResponse> {

        logger.debug("getJobStatuses(userId=$userId)")

        val user = userDAO.validateUserName(userId)

        val response = if (user.adminUser) {
            processedVideos.asMap().values.filter { it.response != null }.mapNotNull { it.jobId to it.response!! }.toMap()
        } else {
            processedVideos.asMap().values.filter { it.userId == userId }.filter { it.response != null }.mapNotNull { it.jobId to it.response!! }.toMap()
        }

        logger.debug("getJobStatuses(userId=$userId) RESPONSE: $response")

        return response
    }

    override fun getJobSummary(userId: Int): Map<String, Int> {

        logger.debug("getJobSummary(userId=$userId)")

        val jobs = getJobStatuses(userId).values.groupingBy { it.success }.eachCount()

        val response = mapOf(STATUS_COMPLETE to (jobs[true] ?: 0), STATUS_FAILURE to (jobs[false] ?: 0),
            STATUS_PENDING to (jobs[null] ?: 0))

        logger.debug("getJobSummary(userId=$userId) RESPONSE: $response")

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

        return if ((existingJob == null) || existingJob.isCancelled()) {

            val uuid = existingJob?.jobId ?: UUID.randomUUID().toString()

            processedVideos.put(query,
                ProcessingJob(
                    userId = userId,
                    jobId = uuid,
                    response = Mp3DownloadResponse(query = query),
                    future = CompletableFuture.runAsync { asyncSearchAndDownload(userId, uuid, query) }
                ))

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

        val result: YoutubeVideo? = downloadVideo(userId, jobId, query, videoList)

        logger.debug("asyncSearchAndDownload(userId=$userId, jobId=$jobId, query=$query) DOWNLOAD RESPONSE: $result")

        historyDAO.save(query, result)
    }

    private fun downloadVideo(userId: Int, jobId: String, query: String, videoList: List<YoutubeVideo>): YoutubeVideo? {
        for (video in videoList) {
            val currentVideo = videoDownloadDAO.initialize(video)
            if (currentVideo != null) {
                val cachedJob = processedVideos.getIfPresent(query)
                if (cachedJob != null) {
                    // Stop downloading new videos if the parent job has been cancelled
                    if (cachedJob.isCancelled()) {
                        break
                    }
                    processedVideos.put(query, cachedJob.copy(Mp3DownloadResponse.ModelMapper.from(currentVideo, query, currentVideo.youtubeDL)))
                } else {
                    processedVideos.put(query,
                        ProcessingJob(
                            userId = userId,
                            jobId = jobId,
                            response = Mp3DownloadResponse.ModelMapper.from(currentVideo, query, currentVideo.youtubeDL)
                        ))
                }
                val downloadedVideo = videoDownloadDAO.download(currentVideo)
                if (downloadedVideo?.filename != null) {
                    processedVideos.put(query,
                        ProcessingJob(
                            userId = userId,
                            jobId = jobId,
                            response = Mp3DownloadResponse.ModelMapper.from(downloadedVideo, query)
                        ))
                    return downloadedVideo
                }
            }
        }
        return null
    }

    private fun getProcessingJob(userId: Int, jobId: String): ProcessingJob? {

        logger.debug("getProcessingJob(userId=$userId, jobId=$jobId)")

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

        val job = if (user.adminUser) {
            filteredJobs.firstOrNull()
        } else {
            filteredJobs.firstOrNull { it.userId == userId }
        }

        logger.debug("getProcessingJob(userId=$userId, jobId=$jobId) RESPONSE: $job")

        return job
    }

    private val STATUS_COMPLETE = "completed"
    private val STATUS_FAILURE = "failed"
    private val STATUS_PENDING = "pending"
}
