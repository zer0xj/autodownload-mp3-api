package com.julien.search.dao

import com.sapher.youtubedl.YoutubeDLRequest

class LocalYoutubeDLRequest(url: String? = null, directory: String? = null) : YoutubeDLRequest() {

    init {
        super.setUrl(url)
        super.setDirectory(directory)
    }

    private val options: MutableMap<String, String?> = HashMap()

    public override fun buildOptions(): String {
        val builder = StringBuilder()

        if (url != null) builder.append("$url ")

        val it: MutableIterator<*> = options.entries.iterator()
        while (it.hasNext()) {
            val (key, value) = it.next() as Map.Entry<String, String?>
            val optionFormatted = String.format("--%s %s", key, value ?: "").trim { it <= ' ' }
            builder.append("$optionFormatted ")
            it.remove()
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
