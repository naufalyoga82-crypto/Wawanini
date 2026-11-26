package id.yoga.catatanutama.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import id.yoga.catatanutama.R
import id.yoga.catatanutama.data.NoteRepository

/** Compatibility adapter used on Android 8–11. Android 12+ uses RemoteCollectionItems. */
class NoteWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        NoteRowsFactory(applicationContext)
}

private class NoteRowsFactory(
    private val context: Context,
) : RemoteViewsService.RemoteViewsFactory {
    private var rows: List<String> = emptyList()

    override fun onCreate() = loadRows()
    override fun onDataSetChanged() = loadRows()
    override fun onDestroy() { rows = emptyList() }
    override fun getCount(): Int = rows.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position !in rows.indices) return null
        return RemoteViews(context.packageName, R.layout.widget_note_row).apply {
            setTextViewText(R.id.widget_row_text, rows[position])
        }
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true

    private fun loadRows() {
        rows = NoteWidgetRows.splitForScrolling(NoteRepository.active(context)?.content.orEmpty())
    }
}
