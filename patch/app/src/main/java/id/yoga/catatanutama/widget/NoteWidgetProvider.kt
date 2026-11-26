package id.yoga.catatanutama.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import id.yoga.catatanutama.MainActivity
import id.yoga.catatanutama.R
import id.yoga.catatanutama.data.NoteRepository

class NoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { updateOne(context, appWidgetManager, it) }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun refreshAll(context: Context) {
            val appContext = context.applicationContext
            val manager = AppWidgetManager.getInstance(appContext)
            val ids = manager.getAppWidgetIds(ComponentName(appContext, NoteWidgetProvider::class.java))
            if (ids.isEmpty()) return

            ids.forEach { updateOne(appContext, manager, it) }

            // API 31+ receives the latest RemoteCollectionItems in updateAppWidget().
            // Older Android versions use RemoteViewsService and require explicit invalidation.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                manager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list)
            }
        }

        private fun updateOne(context: Context, manager: AppWidgetManager, appWidgetId: Int) {
            val note = NoteRepository.active(context)
            val rows = NoteWidgetRows.splitForScrolling(note?.content.orEmpty())
            val views = RemoteViews(context.packageName, R.layout.widget_note)

            views.setTextViewText(
                R.id.widget_title,
                note?.title?.takeIf { it.isNotBlank() } ?: "Catatan Utama",
            )
            views.setViewVisibility(
                R.id.widget_list,
                if (rows.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE,
            )
            views.setViewVisibility(
                R.id.widget_empty,
                if (rows.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE,
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+: populate ListView directly. This avoids binding a
                // separate RemoteViewsService from the launcher.
                val items = RemoteViews.RemoteCollectionItems.Builder()
                    .setHasStableIds(true)
                    .setViewTypeCount(1)
                    .apply {
                        rows.forEachIndexed { index, text ->
                            val row = RemoteViews(context.packageName, R.layout.widget_note_row).apply {
                                setTextViewText(R.id.widget_row_text, text)
                            }
                            addItem(index.toLong(), row)
                        }
                    }
                    .build()
                views.setRemoteAdapter(R.id.widget_list, items)
            } else {
                // Android 8–11 compatibility fallback.
                val serviceIntent = Intent(context, NoteWidgetService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                }
                views.setRemoteAdapter(R.id.widget_list, serviceIntent)
                views.setEmptyView(R.id.widget_list, R.id.widget_empty)
            }

            val openIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                note?.let { putExtra(MainActivity.EXTRA_NOTE_ID, it.id) }
            }
            val openPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId + 4100,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            views.setOnClickPendingIntent(R.id.widget_header, openPendingIntent)
            manager.updateAppWidget(appWidgetId, views)
        }
    }
}
