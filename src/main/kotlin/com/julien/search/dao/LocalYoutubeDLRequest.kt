package com.julien.search.dao

import com.sapher.youtubedl.YoutubeDLRequest

class LocalYoutubeDLRequest(url: String? = null, directory: String? = null) : YoutubeDLRequest() {

    private val options: MutableMap<String, String?> = HashMap()

    init {
        super.setUrl(url)
        super.setDirectory(directory)
        // Add youtube-dl command-line options
        setOption("add-metadata")
        setOption("audio-format", "mp3")
        setOption("audio-quality", "128K")
        setOption("continue")
        setOption("extract-audio")
        setOption("format", "bestaudio")
        setOption("ignore-errors")
        setOption("no-call-home")
        setOption("no-colors")
        setOption("no-warnings")
        setOption("prefer-avconv")
        setOption("prefer-insecure")
        setOption("retries", 10)
        setOption("xattrs")
        setOption("youtube-skip-dash-manifest")
    }

    public override fun buildOptions(): String {
        val builder = StringBuilder()

        if (url != null) {
            builder.append("$url ")
        }

        val it: Iterator<Map.Entry<String, String?>> = options.entries.iterator()
        while (it.hasNext()) {
            val (key, value) = it.next()
            val optionFormatted = String.format("--%s %s", key, value ?: "").trim { it <= ' ' }
            builder.append("$optionFormatted ")
        }
        return builder.toString().trim { it <= ' ' }
    }

    override fun setOption(key: String?) {
        if (key != null) options[key] = null
    }

    override fun setOption(key: String?, value: String?) {
        if (key != null) options[key] = value
    }

    override fun setOption(key: String?, value: Int) {
        if (key != null) options[key] = value.toString()
    }
}
