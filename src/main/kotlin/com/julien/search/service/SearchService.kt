package com.julien.search.service

import com.julien.search.model.JobResponse
import com.julien.search.model.Mp3DownloadResponse
import com.julien.search.model.YoutubeVideo

interface SearchService {

    fun getDownloadStatus(userId: Int, jobId: String): Mp3DownloadResponse?

    fun search(userId: Int, query: String): List<YoutubeVideo>

    fun searchAndDownload(userId: Int, query: String): JobResponse
}
