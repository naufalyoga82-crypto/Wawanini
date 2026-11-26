package id.yoga.catatanutama.widget

/** Splits note content into self-contained pages suitable for a basic RemoteViews widget. */
internal object NoteWidgetPager {
    fun split(text: String, maxChars: Int): List<String> {
        require(maxChars >= 80) { "maxChars must be at least 80" }
        val normalized = text.replace("\r\n", "\n").replace('\r', '\n').trim()
        if (normalized.isBlank()) return emptyList()

        val pages = ArrayList<String>()
        var current = StringBuilder()

        fun flush() {
            val value = current.toString().trim()
            if (value.isNotBlank()) pages += value
            current = StringBuilder()
        }

        val paragraphs = normalized.split(Regex("\\n[\\t ]*\\n+"))
        for (rawParagraph in paragraphs) {
            var remaining = rawParagraph.trim()
            while (remaining.length > maxChars) {
                var cut = remaining.lastIndexOf(' ', maxChars)
                if (cut < maxChars / 2) cut = maxChars
                val part = remaining.substring(0, cut).trim()
                if (part.isNotBlank()) {
                    if (current.isNotEmpty()) flush()
                    pages += part
                }
                remaining = remaining.substring(cut).trimStart()
            }

            if (remaining.isBlank()) continue
            val separator = if (current.isEmpty()) "" else "\n\n"
            if (current.length + separator.length + remaining.length > maxChars) {
                flush()
            }
            if (current.isNotEmpty()) current.append("\n\n")
            current.append(remaining)
        }
        flush()
        return pages
    }
}
