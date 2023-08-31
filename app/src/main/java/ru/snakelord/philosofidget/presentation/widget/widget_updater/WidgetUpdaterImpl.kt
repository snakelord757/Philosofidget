package ru.snakelord.philosofidget.presentation.widget.widget_updater

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import ru.snakelord.philosofidget.presentation.widget.QuotesWidgetProvider

class WidgetUpdaterImpl(private val context: Context) : WidgetUpdater {
    override fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, QuotesWidgetProvider::class.java))
        val updateWidgetIntent = Intent(context, QuotesWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }
        context.sendBroadcast(updateWidgetIntent)
    }
}