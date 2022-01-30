package com.julien.search.dao

import com.sapher.youtubedl.YoutubeDL
import com.sapher.youtubedl.YoutubeDLException
import com.sapher.youtubedl.YoutubeDLResponse
import com.sapher.youtubedl.utils.StreamGobbler
import java.io.File
import java.io.IOException

class LocalYoutubeDL(private val request: LocalYoutubeDLRequest? = null, youtubeDlLocation: String? = null) : YoutubeDL() {

    private var process: Process? = null

    private val stdoutBuffer = StringBuffer()

    init {
        setExecutablePath(youtubeDlLocation)
        stdoutBuffer.append("] 0%")
    }

    fun cancel() = process?.destroyForcibly()

    @Throws(YoutubeDLException::class)
    fun execute(): YoutubeDLResponse = try {
        val command = buildCommand(request!!.buildOptions())
        val startTime = System.nanoTime()
        val split = command.split(" ").toTypedArray()
        val processBuilder = ProcessBuilder(*split)

        if (request.directory != null) {
            processBuilder.directory(File(request.directory))
        }
        process = try {
            processBuilder.start()
        } catch (e: IOException) {
            throw YoutubeDLException(e)
        }
        val outStream = process!!.inputStream
        val errStream = process!!.errorStream
        val stderrBuffer = StringBuffer()
        StreamGobbler(stdoutBuffer, outStream)
        StreamGobbler(stderrBuffer, errStream)
        val exitCode: Int = try {
            process!!.waitFor()
        } catch (e: InterruptedException) {
            throw YoutubeDLException(e)
        }
        if (exitCode > 0) {
            throw YoutubeDLException(stderrBuffer.toString())
        }
        val elapsedTime = ((System.nanoTime() - startTime) / 1000000).toInt()

        YoutubeDLResponse(command, request.option, request.directory, exitCode, elapsedTime, stdoutBuffer.toString(), stderrBuffer.toString())
    } catch (n: NullPointerException) {
        throw YoutubeDLException(n)
    }

    fun getProgress(): String? = stdoutBuffer.toString()
        .substringAfterLast("]")
        .trim()
        .substringBefore(" ")
        .replace(Regex("[^0-9.%]"), "")
        .ifBlank { null }
}
