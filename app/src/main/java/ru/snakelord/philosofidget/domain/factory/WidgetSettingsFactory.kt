package ru.snakelord.philosofidget.domain.factory

import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsFactory {
    suspend fun getWidgetSettings(): Array<WidgetSettings>
}