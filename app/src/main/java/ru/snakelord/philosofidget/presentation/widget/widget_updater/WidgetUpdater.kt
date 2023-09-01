package ru.snakelord.philosofidget.presentation.widget.widget_updater

interface WidgetUpdater {
    fun updateQuote()

    fun updateWidget(widgetPayloads: Set<WidgetPayload>)

    companion object {
        const val WIDGET_PAYLOADS_EXTRA = "WIDGET_PAYLOADS_EXTRA"
    }
}