package com.julien.search.dao

import com.julien.search.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class LocalHistoryDAO : HistoryDAO {

    @Value("\${history.enabled:false}")
    private var enabled: Boolean = false

    @Value("\${history.filename:autodownload-mp3-api.txt}")
    private lateinit var saveFilename: String

    @Value("#{'\${history.location:\${downloads.location:}}'}")
    private lateinit var saveLocation: String

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @Throws(DAOException::class)
    override fun save(query: String, video: YoutubeVideo?) {
        logger.debug("save(query=$query, video=$video")
        if (enabled) {
            // We want to save the history of a search regardless of if a file was saved
            if (!(video?.previouslyDownloaded ?: false)) {
                if (saveLocation.isBlank()) {
                    throw DAOException("History save location is not set", "save(query[$query])", ErrorCode.MISSING_CONFIGURATION)
                }

                try {
                    val historyFile = let {
                        val s = File(saveLocation)
                        return@let if (s.isDirectory) {
                            File(saveLocation +
                                    if (!saveLocation.endsWith("/") && !saveLocation.endsWith("\\")) {
                                        File.separator
                                    } else {
                                        ""
                                    } + saveFilename
                            )
                        } else {
                            s
                        }
                    }

                    if (!historyFile.exists() || !historyFile.canWrite()) {
                        try {
                            if (historyFile.createNewFile()) {
                                logger.debug("Created new file \"${historyFile.name}\"")
                            }
                        } catch (i: IOException) {
                            logger.error("Could not create or write to historyFile[${historyFile.absolutePath}] (query = \"$query\")")
                            return
                        }
                    }

                    historyFile.appendText(query + System.getProperty("line.separator"))
                } catch (e: Exception) {
                    logger.error("Caught ${e.javaClass.simpleName} trying to append \"$query\" to saveLocation[$saveLocation], saveFilename[$saveFilename]:", e)
                    return
                }

                logger.debug("Saved history of query[$query] to saveLocation[$saveLocation], saveFilename[$saveFilename]")
            } else {
                logger.debug("Already downloaded \"${video?.filename}\" for search query[$query]. Not saving history " +
                        "of repeat search.")
            }
        }
    }
}
