package com.julien.search.dao

import com.julien.search.model.YoutubeVideo

interface VideoDownloadDAO {

    fun download(video: YoutubeVideo): YoutubeVideo?
}
