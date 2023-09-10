package ru.snakelord.philosofidget.presentation.widget.widget_manager

interface WidgetManager {
    fun updateWidget(widgetPayloads: Set<WidgetPayload>)

    fun updateQuote()

    fun addWidgetOnHomeScreen()

    fun hasActiveWidgets(): Boolean

    companion object {
        const val WIDGET_PAYLOADS_EXTRA = "WIDGET_PAYLOADS_EXTRA"
    }
}