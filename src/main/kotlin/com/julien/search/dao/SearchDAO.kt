package com.julien.search.dao

import com.julien.search.model.YoutubeVideo

interface SearchDAO {

    fun search(query: String): List<YoutubeVideo>
}
