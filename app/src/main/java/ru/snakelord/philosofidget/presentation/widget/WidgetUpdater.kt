package ru.snakelord.philosofidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class WidgetUpdater(private val context: Context) {
    fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, QuotesWidgetProvider::class.java))
        val updateWidgetIntent = Intent(context, QuotesWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }
        context.sendBroadcast(updateWidgetIntent)
    }
}