package id.yoga.catatanutama.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteWidgetPagerTest {
    @Test
    fun blankTextHasNoPages() {
        assertTrue(NoteWidgetPager.split("  \n\n  ", 180).isEmpty())
    }

    @Test
    fun shortNoteUsesSinglePage() {
        assertEquals(listOf("Catatan pendek"), NoteWidgetPager.split("Catatan pendek", 180))
    }

    @Test
    fun longNoteIsSplitAndAllWordsArePreserved() {
        val source = (1..300).joinToString(" ") { "kata$it" }
        val pages = NoteWidgetPager.split(source, 180)
        assertTrue(pages.size > 1)
        assertEquals(source, pages.joinToString(" "))
        assertTrue(pages.all { it.length <= 180 })
    }

    @Test
    fun paragraphsAreKeptReadable() {
        assertEquals(
            listOf("Paragraf pertama\n\nParagraf kedua"),
            NoteWidgetPager.split("Paragraf pertama\n\nParagraf kedua", 180),
        )
    }

    @Test
    fun oversizedSingleWordDoesNotLoseData() {
        val source = "x".repeat(450)
        val pages = NoteWidgetPager.split(source, 180)
        assertEquals(3, pages.size)
        assertEquals(source, pages.joinToString(""))
    }
}
