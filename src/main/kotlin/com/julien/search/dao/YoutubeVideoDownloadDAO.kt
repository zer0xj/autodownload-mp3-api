package com.julien.search.dao

import com.julien.search.model.ErrorCode
import com.julien.search.model.YoutubeVideo
import com.sapher.youtubedl.YoutubeDL
import com.sapher.youtubedl.YoutubeDLRequest
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
    override fun download(video: YoutubeVideo): YoutubeVideo? {
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
                        previouslyDownloaded = true
                    )
                }
            }

            val request = YoutubeDLRequest(video.url, downloadLocation)

            // Add youtube-dl command-line options
            request.setOption("add-metadata")
            request.setOption("audio-format", "mp3")
            request.setOption("audio-quality", "128K")
            request.setOption("continue")
            request.setOption("extract-audio")
            request.setOption("format", "bestaudio")
            request.setOption("ignore-errors")
            request.setOption("no-call-home")
            request.setOption("no-colors")
            request.setOption("no-warnings")
            request.setOption("prefer-avconv")
            request.setOption("prefer-insecure")
            request.setOption("retries", 10)
            request.setOption("xattrs")
            request.setOption("youtube-skip-dash-manifest")

            YoutubeDL.setExecutablePath(youtubeDlLocation)

            val response = YoutubeDL.execute(request)

            return YoutubeVideo(
                id = video.id,
                filename = response.out.substringAfterLast("Destination: ").substringBefore("\n"),
                previouslyDownloaded = false,
                title = video.title
            )
        } catch (e: Exception) {
            logger.error("Caught ${e.javaClass.simpleName} trying to download from \"${video.url}\":", e)
            // Throw DAOException here
            return null
        }
    }

    internal class Mp3FilterFilter : FilenameFilter {
        override fun accept(dir: File, fileName: String): Boolean {
            return fileName.endsWith(".mp3")
        }
    }
}
