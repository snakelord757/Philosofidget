package ru.snakelord.philosofidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.RemoteViews
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.presentation.service.QuoteLoadingService
import ru.snakelord.philosofidget.presentation.view.WidgetViewDelegate

class QuotesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val widgetViewDelegate = WidgetViewDelegate(RemoteViews(context.packageName, R.layout.widget_quote))
            val serviceIntent = Intent(context, QuoteLoadingService::class.java)
            context.applicationContext.bindService(
                serviceIntent,
                QuoteLoadingServiceConnection(appWidgetId, widgetViewDelegate) {
                    appWidgetManager.updateAppWidget(appWidgetId, widgetViewDelegate.remoteViews)
                },
                BIND_AUTO_CREATE
            )
        }
    }

    private class QuoteLoadingServiceConnection(
        private val widgetId: Int,
        private val widgetViewDelegate: WidgetViewDelegate,
        private val onQuoteLoadedCallback: (() -> Unit)?
    ) : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val quoteServiceDelegate = (service as? QuoteLoadingService.QuoteLoadingServiceBinder)?.getQuoteServiceDelegate()
            quoteServiceDelegate?.let {
                it.addWidgetId(widgetId)
                it.addWidgetViewDelegate(widgetViewDelegate)
                it.addOnQuoteLoadedCallback(onQuoteLoadedCallback)
                it.loadQuote()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) = Unit
    }
}