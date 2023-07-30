package ru.snakelord.philosofidget.presentation.service

import ru.snakelord.philosofidget.presentation.view.WidgetViewDelegate

interface QuoteServiceDelegate {
    fun loadQuote()
    fun addWidgetId(widgetId: Int)
    fun addWidgetViewDelegate(widgetViewDelegate: WidgetViewDelegate)
    fun addOnQuoteLoadedCallback(onQuoteLoadedCallback: (() -> Unit)? = null)
}