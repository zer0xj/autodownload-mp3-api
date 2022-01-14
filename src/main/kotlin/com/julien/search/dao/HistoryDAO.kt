package com.julien.search.dao

import com.julien.search.model.YoutubeVideo

interface HistoryDAO {

    fun save(query: String, video: YoutubeVideo?)
}
