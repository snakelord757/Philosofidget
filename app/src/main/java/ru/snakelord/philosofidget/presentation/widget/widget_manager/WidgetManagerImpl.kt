package ru.snakelord.philosofidget.presentation.widget.widget_manager

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import ru.snakelord.philosofidget.presentation.widget.QuotesWidgetProvider
import ru.snakelord.philosofidget.presentation.widget.widget_updater.WidgetPayload
import ru.snakelord.philosofidget.presentation.widget.widget_updater.WidgetUpdater

class WidgetManagerImpl(private val context: Context) : WidgetManager {
    override fun updateWidget(widgetPayloads: Set<WidgetPayload>) {
        val appWidgetIds = getAppWidgetsIds()
        val updateWidgetIntent = Intent(context, QuotesWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            if (widgetPayloads.isNotEmpty()) putExtra(WidgetUpdater.WIDGET_PAYLOADS_EXTRA, widgetPayloads.toTypedArray())
        }
        context.sendBroadcast(updateWidgetIntent)
    }

    override fun updateQuote() = updateWidget(setOf(WidgetPayload.QUOTE))

    override fun hasActiveWidgets(): Boolean = getAppWidgetsIds().isNotEmpty()

    private fun getAppWidgetsIds(): IntArray =
        AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, QuotesWidgetProvider::class.java))
}