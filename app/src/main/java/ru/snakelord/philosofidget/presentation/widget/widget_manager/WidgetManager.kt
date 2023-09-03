package ru.snakelord.philosofidget.presentation.widget.widget_manager

import ru.snakelord.philosofidget.presentation.widget.widget_updater.WidgetPayload

interface WidgetManager {
    fun updateWidget(widgetPayloads: Set<WidgetPayload>)

    fun updateQuote()

    fun hasActiveWidgets(): Boolean
}