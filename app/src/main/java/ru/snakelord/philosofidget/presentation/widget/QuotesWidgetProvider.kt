package ru.snakelord.philosofidget.presentation.widget

import android.app.PendingIntent
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
import ru.snakelord.philosofidget.presentation.view.MainActivity

class QuotesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_quote)
            val applicationContext = context.applicationContext
            setupOnWidgetClickListener(applicationContext, remoteViews)
            startService(
                applicationContext,
                appWidgetManager,
                appWidgetId,
                WidgetViewDelegate(remoteViews)
            )
        }
    }

    private fun setupOnWidgetClickListener(context: Context, remoteViews: RemoteViews) {
        remoteViews.setOnClickPendingIntent(
            R.id.widgetRoot,
            PendingIntent.getActivities(
                context,
                0,
                arrayOf(Intent(context, MainActivity::class.java)),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun startService(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int,
        widgetViewDelegate: WidgetViewDelegate
    ) {
        val serviceIntent = Intent(context, QuoteLoadingService::class.java)
        context.bindService(
            serviceIntent,
            QuoteLoadingServiceConnection(widgetId, widgetViewDelegate) {
                appWidgetManager.updateAppWidget(widgetId, widgetViewDelegate.remoteViews)
            },
            BIND_AUTO_CREATE
        )
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