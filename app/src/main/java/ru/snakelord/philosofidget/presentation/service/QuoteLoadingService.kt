package ru.snakelord.philosofidget.presentation.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.domain.ext.viewModel
import ru.snakelord.philosofidget.presentation.model.QuoteWidgetState
import ru.snakelord.philosofidget.presentation.widget.WidgetViewDelegate

class QuoteLoadingService : ServiceWithVMStorage(), QuoteServiceDelegate {

    private val quoteViewModel by viewModel<QuoteViewModel>()
    private var widgetId: Int = UNDEFINED_ID
    private var widgetViewDelegate: WidgetViewDelegate? = null
    private var onQuoteLoadedCallback: (() -> Unit)? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return QuoteLoadingServiceBinder()
    }

    override fun loadQuote() {
        if (widgetId == UNDEFINED_ID) error("Widget id isn't provided")
        quoteViewModel.loadQuote()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                quoteViewModel.quoteState.collect(::setWidgetState)
            }
        }
    }

    private fun setWidgetState(quoteWidgetState: QuoteWidgetState) {
        widgetViewDelegate?.setProgressVisibility(quoteWidgetState is QuoteWidgetState.Loading)
        if (quoteWidgetState is QuoteWidgetState.WidgetState) {
            widgetViewDelegate?.setQuote(quoteWidgetState.quote)
            widgetViewDelegate?.isAuthorVisible(quoteWidgetState.isAuthorVisible)
            onQuoteLoadedCallback?.invoke()
            stopSelf()
        }
    }

    override fun addWidgetId(widgetId: Int) {
        this.widgetId = widgetId
    }

    override fun addWidgetViewDelegate(widgetViewDelegate: WidgetViewDelegate) {
        this.widgetViewDelegate = widgetViewDelegate
    }

    override fun addOnQuoteLoadedCallback(onQuoteLoadedCallback: (() -> Unit)?) {
        this.onQuoteLoadedCallback = onQuoteLoadedCallback
    }

    inner class QuoteLoadingServiceBinder : Binder() {
        fun getQuoteServiceDelegate() = this@QuoteLoadingService as QuoteServiceDelegate
    }

    private companion object {
        const val UNDEFINED_ID = -1
    }
}