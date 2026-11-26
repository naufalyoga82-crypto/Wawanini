package id.yoga.catatanutama.widget

/** Turns long note text into reasonably sized, readable widget rows. */
internal object NoteWidgetRows {
    private const val DEFAULT_MAX_CHARS = 520
    private const val MIN_PREFERRED_BREAK = 240

    fun splitForScrolling(text: String, maxChars: Int = DEFAULT_MAX_CHARS): List<String> {
        require(maxChars >= MIN_PREFERRED_BREAK)
        val normalized = text.replace("\r\n", "\n").replace('\r', '\n').trim()
        if (normalized.isBlank()) return emptyList()

        val result = ArrayList<String>()
        val paragraphs = normalized.split(Regex("\\n[\\t ]*\\n+"))
        for (paragraph in paragraphs) {
            var remaining = paragraph.trim()
            while (remaining.length > maxChars) {
                var cut = remaining.lastIndexOfAny(charArrayOf(' ', '\n', '\t'), maxChars)
                if (cut < MIN_PREFERRED_BREAK) cut = maxChars
                result += remaining.substring(0, cut).trim()
                remaining = remaining.substring(cut).trimStart()
            }
            if (remaining.isNotBlank()) result += remaining
        }
        return result
    }
}
