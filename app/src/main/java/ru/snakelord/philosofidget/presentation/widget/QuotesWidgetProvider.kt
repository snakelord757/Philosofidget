package ru.snakelord.philosofidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.usecase.quote.GetStoredQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetUpdateTimeUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.RemoveStoredQuoteUseCase
import ru.snakelord.philosofidget.presentation.mapper.QuoteWidgetStateMapper
import ru.snakelord.philosofidget.presentation.model.QuoteWidgetState
import ru.snakelord.philosofidget.presentation.widget.view_delegate.WidgetViewDelegate
import ru.snakelord.philosofidget.presentation.widget.worker.QuoteLoadingWorker

class QuotesWidgetProvider : CoroutineAppWidgetProvider(), KoinComponent {

    private val getStoreQuoteUseCase by inject<GetStoredQuoteUseCase>()
    private val widgetSettingsInteractor by inject<WidgetSettingsInteractor>()
    private val quoteWidgetStateMapper by inject<QuoteWidgetStateMapper>()
    private val widgetViewDelegate by inject<WidgetViewDelegate>()
    private val removeStoredQuoteUseCase by inject<RemoveStoredQuoteUseCase>()
    private val getUpdateTimeUseCase by inject<GetUpdateTimeUseCase>()

    override fun onEnabled(context: Context) = startQuoteLoadingWorker(context)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { widgetId ->
            ioScope.launch {
                setWidgetState(QuoteWidgetState.Loading, appWidgetManager, widgetId)
                val quoteWidgetParams = widgetSettingsInteractor.getQuoteWidgetParams()
                val shouldRestart = widgetViewDelegate.selectedLanguage != quoteWidgetParams.quoteLang
                if (shouldRestart) {
                    widgetViewDelegate.selectedLanguage = quoteWidgetParams.quoteLang
                    startQuoteLoadingWorker(context, true)
                } else {
                    val quote = getStoreQuoteUseCase.invoke() ?: return@launch
                    val quoteWidgetState = quoteWidgetStateMapper.map(quote, quoteWidgetParams)
                    setWidgetState(quoteWidgetState, appWidgetManager, widgetId)
                }
            }
        }
    }

    private fun setWidgetState(quoteWidgetState: QuoteWidgetState, appWidgetManager: AppWidgetManager, appWidgetId: Int) = with(widgetViewDelegate) {
        mainScope.launch {
            setProgressVisibility(quoteWidgetState is QuoteWidgetState.Loading)
            if (quoteWidgetState is QuoteWidgetState.WidgetState) {
                setQuote(quoteWidgetState.quote)
                isAuthorVisible(quoteWidgetState.isAuthorVisible)
                setQuoteTextSize(quoteWidgetState.quoteTextSize)
                setQuoteAuthorTextSize(quoteWidgetState.quoteAuthorTextSize)
            }
            appWidgetManager.updateAppWidget(appWidgetId, widgetView)
        }
    }

    private fun startQuoteLoadingWorker(
        context: Context,
        shouldRestart: Boolean = false
    ) {
        ioScope.launch {
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    LOAD_QUOTE_WORKER_NAME,
                    if (shouldRestart) ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE else ExistingPeriodicWorkPolicy.UPDATE,
                    QuoteLoadingWorker.createQuoteWidgetWorker(getUpdateTimeUseCase.invoke())
                )
        }
    }

    override fun onDisabled(context: Context) {
        ioScope.launch { removeStoredQuoteUseCase.invoke() }
        WorkManager.getInstance(context).cancelUniqueWork(LOAD_QUOTE_WORKER_NAME)
        super.onDisabled(context)
    }

    private companion object {
        const val LOAD_QUOTE_WORKER_NAME = "LOAD_QUOTE_WORKER_NAME"
    }
}