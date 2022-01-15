package com.julien.search.service

import com.julien.search.model.YoutubeVideo

interface SearchService {
    fun search(userId: Int, query: String): List<YoutubeVideo>

    fun searchAndDownload(userId: Int, query: String): YoutubeVideo?
}
