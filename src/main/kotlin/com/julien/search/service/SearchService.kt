package com.julien.search.service

import com.julien.search.model.Mp3DownloadResponse
import com.julien.search.model.ProcessingJob
import com.julien.search.model.YoutubeVideo

interface SearchService {

    fun getJobStatus(userId: Int, jobId: String): Mp3DownloadResponse?

    fun getJobSummary(userId: Int): Map<String, Int>

    fun getProcessingJobs(userId: Int): List<Mp3DownloadResponse>

    fun search(userId: Int, query: String): List<YoutubeVideo>

    fun searchAndDownload(userId: Int, query: String): ProcessingJob
}
