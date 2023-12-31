package ru.snakelord.philosofidget.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase
import ru.snakelord.philosofidget.presentation.common.UseCases
import ru.snakelord.philosofidget.presentation.mapper.QuoteWidgetStateMapper
import ru.snakelord.philosofidget.presentation.model.WidgetState
import ru.snakelord.philosofidget.presentation.widget.view_delegate.WidgetViewDelegate
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetPayload
import ru.snakelord.philosofidget.presentation.widget.worker.QuoteLoadingWorker

class QuotesWidgetProvider : BaseAppWidgetProvider(), KoinComponent {

    private val getStoredQuoteUseCase by inject<CoroutineUseCase<Quote?>>(named(UseCases.GET_STORED_QUOTE))
    private val widgetSettingsInteractor by inject<WidgetSettingsInteractor>()
    private val quoteWidgetStateMapper by inject<QuoteWidgetStateMapper>()
    private val widgetViewDelegate by inject<WidgetViewDelegate>()
    private val removeStoredQuoteUseCase by inject<CoroutineUseCase<Unit>>(named(UseCases.REMOVE_STORED_QUOTE))
    private val getUpdateTimeUseCase by inject<CoroutineUseCase<Long>>(named(UseCases.GET_UPDATE_TIME))
    private val removeQuoteParamsUseCase by inject<CoroutineUseCase<Unit>>(named(UseCases.REMOVE_WIDGET_PARAMS))

    override fun onEnabled(context: Context) = doOnIo {
        if (getStoredQuote() == null) startQuoteLoadingWorker(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) = appWidgetIds.forEach { widgetId ->
        doOnIo {
            val quoteWidgetParams = widgetSettingsInteractor.getQuoteWidgetParams()
            val quote = getStoredQuote() ?: return@doOnIo
            val quoteWidgetState = quoteWidgetStateMapper.map(quote, quoteWidgetParams)
            doOnMain {
                setupWidgetView(context, quoteWidgetState)
                appWidgetManager.updateAppWidget(widgetId, widgetViewDelegate.widgetView)
            }
        }
    }

    private fun setupWidgetView(context: Context, widgetState: WidgetState) {
        handlePayloads(widgetState)
        val isLanguageChanged = payloads.contains(WidgetPayload.QUOTE_LANGUAGE)
        val isUpdateTimeChanged = payloads.contains(WidgetPayload.QUOTE_UPDATE_TIME)
        if (isLanguageChanged || isUpdateTimeChanged) startQuoteLoadingWorker(context = context, shouldRestart = isLanguageChanged)
    }

    private fun handlePayloads(widgetState: WidgetState) = with(widgetViewDelegate) {
        payloads.forEach {
            when (it) {
                WidgetPayload.QUOTE -> setupWidget(widgetState)
                WidgetPayload.QUOTE_AUTHOR_TEXT_GRAVITY -> setQuoteAuthorTextGravity(widgetState.quoteAuthorTextGravity)
                WidgetPayload.QUOTE_TEXT_SIZE -> setQuoteTextSize(widgetState.quoteTextSize)
                WidgetPayload.AUTHOR_VISIBILITY -> isAuthorVisible(widgetState.isAuthorVisible)
                WidgetPayload.AUTHOR_TEXT_SIZE -> setQuoteAuthorTextSize(widgetState.quoteAuthorTextSize)
                WidgetPayload.QUOTE_TEXT_GRAVITY -> setQuoteTextGravity(widgetState.quoteTextGravity)
                WidgetPayload.QUOTE_TEXT_COLOR -> setQuoteTextColor(widgetState.quoteTextColor)
                WidgetPayload.QUOTE_AUTHOR_TEXT_COLOR -> setQuoteAuthorTextColor(widgetState.quoteAuthorTextColor)
                else -> Unit
            }
        }
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
        doOnIo {
            removeStoredQuoteUseCase.invoke()
            removeQuoteParamsUseCase.invoke()
        }
        WorkManager.getInstance(context).cancelUniqueWork(LOAD_QUOTE_WORKER_NAME)
        super.onDisabled(context)
    }

    private suspend fun getStoredQuote() = getStoredQuoteUseCase.invoke()

    private companion object {
        const val LOAD_QUOTE_WORKER_NAME = "LOAD_QUOTE_WORKER_NAME"
    }
}