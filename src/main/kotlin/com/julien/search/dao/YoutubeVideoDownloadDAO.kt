package com.julien.search.dao

import com.julien.search.model.ErrorCode
import com.julien.search.model.YoutubeVideo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FilenameFilter


@Service
class YoutubeVideoDownloadDAO : VideoDownloadDAO {

    @Value("\${downloads.location:}")
    private lateinit var downloadLocation: String

    @Value("\${youtube-dl.location:/usr/bin/youtube-dl}")
    private lateinit var youtubeDlLocation: String

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @Throws(DAOException::class)
    override fun initialize(video: YoutubeVideo): YoutubeVideo? {
        try {
            if (video.id == null) {
                logger.error("ID is null for video[$video]")
                return null
            }

            if (downloadLocation.isBlank()) {
                throw DAOException("Download location not set", "download(videoId[${video.id}])", ErrorCode.MISSING_CONFIGURATION)
            }

            for (filename in File(downloadLocation).list(Mp3FilterFilter())?.asList() ?: emptyList()) {
                if (filename.contains(video.id)) {
                    logger.info("Already downloaded this song as \"$filename\"")
                    return YoutubeVideo(
                        id = video.id,
                        title = video.title,
                        filename = filename,
                        previouslyDownloaded = true,
                        youtubeDL = LocalYoutubeDL()
                    )
                }
            }

            val request = LocalYoutubeDLRequest(video.url, downloadLocation)

            return YoutubeVideo(
                id = video.id,
                previouslyDownloaded = false,
                title = video.title,
                youtubeDL = LocalYoutubeDL(request, youtubeDlLocation)
            )
        } catch (e: Exception) {
            logger.error("Caught ${e.javaClass.simpleName} trying to download from \"${video.url}\":", e)
            // Throw DAOException here
            return null
        }
    }

    @Throws(DAOException::class)
    override fun download(video: YoutubeVideo): YoutubeVideo? = if (!video.previouslyDownloaded) {
        try {
            val response = video.youtubeDL!!.execute()

            YoutubeVideo(
                id = video.id,
                filename = response.out.substringAfterLast("Destination: ").substringBefore("\n"),
                previouslyDownloaded = false,
                title = video.title
            )
        } catch (e: Exception) {
            logger.error("Caught ${e.javaClass.simpleName} trying to download from \"${video.url}\":", e)
            // Throw DAOException here?
            null
        }
    } else {
        video
    }

    internal class Mp3FilterFilter : FilenameFilter {
        override fun accept(dir: File, fileName: String): Boolean {
            return fileName.endsWith(".mp3")
        }
    }
}
