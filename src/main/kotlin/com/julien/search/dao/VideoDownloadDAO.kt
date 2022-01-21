package com.julien.search.dao

import com.julien.search.model.YoutubeVideo

interface VideoDownloadDAO {

    fun initialize(video: YoutubeVideo): YoutubeVideo?

    fun download(video: YoutubeVideo): YoutubeVideo?
}
