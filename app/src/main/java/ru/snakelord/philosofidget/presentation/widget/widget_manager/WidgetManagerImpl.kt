package ru.snakelord.philosofidget.presentation.widget.widget_manager

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.os.bundleOf
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver
import ru.snakelord.philosofidget.presentation.common.toaster.Toaster
import ru.snakelord.philosofidget.presentation.widget.QuotesWidgetProvider

class WidgetManagerImpl(private val context: Context, private val stringResolver: StringResolver) : WidgetManager {

    private val appWidgetManager = AppWidgetManager.getInstance(context)

    override fun updateWidget(widgetPayloads: Set<WidgetPayload>) {
        val appWidgetIds = getAppWidgetsIds()
        val updateWidgetIntent = Intent(context, QuotesWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            if (widgetPayloads.isNotEmpty()) putExtra(WidgetManager.WIDGET_PAYLOADS_EXTRA, widgetPayloads.toTypedArray())
        }
        context.sendBroadcast(updateWidgetIntent)
    }

    override fun updateQuote() = updateWidget(setOf(WidgetPayload.QUOTE))
    override fun addWidgetOnHomeScreen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            appWidgetManager.requestPinAppWidget(ComponentName(context, QuotesWidgetProvider::class.java), null, null)
        } else {
            Toaster.toast(context, stringResolver.getString(R.string.add_widget_on_home_screen_error))
        }
    }

    override fun hasActiveWidgets(): Boolean = getAppWidgetsIds().isNotEmpty()

    private fun getAppWidgetsIds(): IntArray = appWidgetManager.getAppWidgetIds(ComponentName(context, QuotesWidgetProvider::class.java))
}