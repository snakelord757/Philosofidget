package ru.snakelord.philosofidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.usecase.quote.GetStoredQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetUpdateTimeUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.RemoveStoredQuoteUseCase
import ru.snakelord.philosofidget.presentation.mapper.QuoteWidgetStateMapper
import ru.snakelord.philosofidget.presentation.model.QuoteWidgetState
import ru.snakelord.philosofidget.presentation.widget.view_delegate.WidgetViewDelegate
import ru.snakelord.philosofidget.presentation.widget.widget_updater.WidgetPayload
import ru.snakelord.philosofidget.presentation.widget.worker.QuoteLoadingWorker

class QuotesWidgetProvider : BaseAppWidgetProvider(), KoinComponent {

    private val getStoreQuoteUseCase by inject<GetStoredQuoteUseCase>()
    private val widgetSettingsInteractor by inject<WidgetSettingsInteractor>()
    private val quoteWidgetStateMapper by inject<QuoteWidgetStateMapper>()
    private val widgetViewDelegate by inject<WidgetViewDelegate>()
    private val removeStoredQuoteUseCase by inject<RemoveStoredQuoteUseCase>()
    private val getUpdateTimeUseCase by inject<GetUpdateTimeUseCase>()

    override fun onEnabled(context: Context) = startQuoteLoadingWorker(context)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { widgetId ->
            doOnIo {
                setWidgetState(
                    context = context,
                    quoteWidgetState = QuoteWidgetState.Loading,
                    appWidgetManager = appWidgetManager,
                    appWidgetId = widgetId
                )
                val quoteWidgetParams = widgetSettingsInteractor.getQuoteWidgetParams()
                val quote = getStoreQuoteUseCase.invoke() ?: return@doOnIo
                val quoteWidgetState = quoteWidgetStateMapper.map(quote, quoteWidgetParams)
                setWidgetState(
                    context = context,
                    quoteWidgetState = quoteWidgetState,
                    appWidgetManager = appWidgetManager,
                    appWidgetId = widgetId
                )
            }
        }
    }

    private fun setWidgetState(
        context: Context,
        quoteWidgetState: QuoteWidgetState,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) = doOnMain {
        widgetViewDelegate.setProgressVisibility(quoteWidgetState is QuoteWidgetState.Loading)
        if (quoteWidgetState is QuoteWidgetState.WidgetState) setupWidgetView(quoteWidgetState, context)
        appWidgetManager.updateAppWidget(appWidgetId, widgetViewDelegate.widgetView)
    }

    private fun setupWidgetView(widgetState: QuoteWidgetState.WidgetState, context: Context) {
        payloads.forEach {
            when (it) {
                WidgetPayload.QUOTE -> widgetViewDelegate.setQuote(widgetState.quote)
                WidgetPayload.QUOTE_TEXT_SIZE -> widgetViewDelegate.setQuoteTextSize(widgetState.quoteTextSize)
                WidgetPayload.AUTHOR_VISIBILITY -> widgetViewDelegate.setQuoteTextSize(widgetState.quoteTextSize)
                WidgetPayload.AUTHOR_TEXT_SIZE -> widgetViewDelegate.isAuthorVisible(widgetState.isAuthorVisible)
                else -> Unit
            }
        }
        val isLanguageChanged = payloads.contains(WidgetPayload.QUOTE_LANGUAGE)
        val isUpdateTimeChanged = payloads.contains(WidgetPayload.QUOTE_UPDATE_TIME)
        val shouldRestartWorker = (isLanguageChanged || isUpdateTimeChanged) && payloads.contains(WidgetPayload.QUOTE).not()
        if (shouldRestartWorker) startQuoteLoadingWorker(context = context, shouldRestart = isLanguageChanged)
    }

    private fun startQuoteLoadingWorker(
        context: Context,
        shouldRestart: Boolean = false
    ) = doOnIo {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            LOAD_QUOTE_WORKER_NAME,
            if (shouldRestart) ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE else ExistingPeriodicWorkPolicy.UPDATE,
            QuoteLoadingWorker.createQuoteWidgetWorker(getUpdateTimeUseCase.invoke())
        )
    }

    override fun onDisabled(context: Context) {
        doOnIo { removeStoredQuoteUseCase.invoke() }
        WorkManager.getInstance(context).cancelUniqueWork(LOAD_QUOTE_WORKER_NAME)
        super.onDisabled(context)
    }

    private companion object {
        const val LOAD_QUOTE_WORKER_NAME = "LOAD_QUOTE_WORKER_NAME"
    }
}