package id.yoga.catatanutama.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteWidgetRowsTest {
    @Test
    fun emptyTextProducesNoRows() {
        assertTrue(NoteWidgetRows.splitForScrolling("  \n\n  ").isEmpty())
    }

    @Test
    fun shortTextRemainsOneRow() {
        assertEquals(listOf("Catatan singkat"), NoteWidgetRows.splitForScrolling("Catatan singkat"))
    }

    @Test
    fun longTextIsSplitWithoutLosingCharacters() {
        val source = (1..80).joinToString(" ") { "kalimat$it" }
        val rows = NoteWidgetRows.splitForScrolling(source, maxChars = 260)
        assertTrue(rows.size > 1)
        assertEquals(source, rows.joinToString(" "))
        assertTrue(rows.all { it.length <= 260 })
    }

    @Test
    fun blankLinesCreateSeparateReadableRows() {
        assertEquals(
            listOf("Paragraf pertama", "Paragraf kedua"),
            NoteWidgetRows.splitForScrolling("Paragraf pertama\n\nParagraf kedua"),
        )
    }
}
