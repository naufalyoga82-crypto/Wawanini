package id.yoga.catatanutama.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import id.yoga.catatanutama.MainActivity
import id.yoga.catatanutama.R
import id.yoga.catatanutama.data.NoteRepository

/**
 * Compatibility-first widget. It deliberately avoids collection adapters:
 * long notes are paged through standard TextViews and Previous/Next actions.
 */
class NoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateOne(context, manager, it) }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        manager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        updateOne(context, manager, appWidgetId)
        super.onAppWidgetOptionsChanged(context, manager, appWidgetId, newOptions)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        appWidgetIds.forEach { editor.remove(pageKey(it)) }
        editor.apply()
        super.onDeleted(context, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != ACTION_NEXT && intent.action != ACTION_PREVIOUS) return
        val id = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        )
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return

        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getInt(pageKey(id), 0)
        val next = if (intent.action == ACTION_NEXT) current + 1 else (current - 1).coerceAtLeast(0)
        prefs.edit().putInt(pageKey(id), next).apply()
        updateOne(context, AppWidgetManager.getInstance(context), id)
    }

    companion object {
        private const val PREFS = "catatan_widget_pages"
        private const val ACTION_NEXT = "id.yoga.catatanutama.action.NEXT_PAGE"
        private const val ACTION_PREVIOUS = "id.yoga.catatanutama.action.PREVIOUS_PAGE"

        fun refreshAll(context: Context) {
            val app = context.applicationContext
            val manager = AppWidgetManager.getInstance(app)
            val ids = manager.getAppWidgetIds(ComponentName(app, NoteWidgetProvider::class.java))
            ids.forEach { updateOne(app, manager, it) }
        }

        private fun updateOne(context: Context, manager: AppWidgetManager, appWidgetId: Int) {
            val note = runCatching { NoteRepository.active(context) }.getOrNull()
            val options = manager.getAppWidgetOptions(appWidgetId)
            val width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250).coerceAtLeast(180)
            val height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 230).coerceAtLeast(180)
            val lines = ((height - 100) / 20).coerceIn(5, 12)
            val pageCapacity = (width / 7 * lines).coerceIn(180, 620)
            val pages = NoteWidgetPager.split(note?.content.orEmpty(), pageCapacity)

            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val saved = prefs.getInt(pageKey(appWidgetId), 0)
            val current = if (pages.isEmpty()) 0 else saved.coerceIn(0, pages.lastIndex)
            if (saved != current) prefs.edit().putInt(pageKey(appWidgetId), current).apply()

            val views = RemoteViews(context.packageName, R.layout.widget_note_paged)
            views.setTextViewText(
                R.id.widget_title,
                note?.title?.takeIf { it.isNotBlank() } ?: "Catatan Utama",
            )
            val body = when {
                note == null -> "Belum ada catatan. Buka aplikasi, tulis catatan, lalu simpan."
                pages.isEmpty() -> "Catatan ini belum memiliki isi."
                else -> pages[current]
            }
            views.setTextViewText(R.id.widget_body, body)
            views.setTextViewText(
                R.id.widget_page,
                if (pages.isEmpty()) "—" else "Bagian ${current + 1} / ${pages.size}",
            )
            views.setViewVisibility(R.id.widget_previous, if (current > 0) View.VISIBLE else View.INVISIBLE)
            views.setViewVisibility(
                R.id.widget_next,
                if (current < pages.lastIndex) View.VISIBLE else View.INVISIBLE,
            )
            views.setTextViewText(
                R.id.widget_hint,
                if (pages.size > 1) "Ketuk Berikutnya untuk membaca lanjut"
                else "Isi catatan tampil langsung di layar utama",
            )

            views.setOnClickPendingIntent(R.id.widget_header, openPendingIntent(context, appWidgetId, note?.id))
            views.setOnClickPendingIntent(R.id.widget_previous, pagePendingIntent(context, appWidgetId, ACTION_PREVIOUS))
            views.setOnClickPendingIntent(R.id.widget_next, pagePendingIntent(context, appWidgetId, ACTION_NEXT))
            views.setOnClickPendingIntent(R.id.widget_body, pagePendingIntent(context, appWidgetId, ACTION_NEXT))
            manager.updateAppWidget(appWidgetId, views)
        }

        private fun openPendingIntent(context: Context, widgetId: Int, noteId: String?): PendingIntent {
            val open = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                if (noteId != null) putExtra(MainActivity.EXTRA_NOTE_ID, noteId)
            }
            return PendingIntent.getActivity(
                context, 9000 + widgetId, open,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private fun pagePendingIntent(context: Context, widgetId: Int, action: String): PendingIntent {
            val command = Intent(context, NoteWidgetProvider::class.java).apply {
                this.action = action
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            val code = if (action == ACTION_NEXT) 10000 + widgetId else 11000 + widgetId
            return PendingIntent.getBroadcast(
                context, code, command,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private fun pageKey(id: Int) = "page_$id"
    }
}
