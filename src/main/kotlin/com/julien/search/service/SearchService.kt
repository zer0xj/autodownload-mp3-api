package com.julien.search.service

import com.julien.search.model.YoutubeVideo

interface SearchService {
    fun search(query: String): List<YoutubeVideo>

    fun searchAndDownload(query: String): YoutubeVideo?
}
