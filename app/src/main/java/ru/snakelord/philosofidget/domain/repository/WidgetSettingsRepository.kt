package ru.snakelord.philosofidget.domain.repository

import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsRepository {
    fun getAuthorVisibility(): Boolean

    fun setAuthorVisibility(isAuthorVisible: Boolean)

    fun getWidgetSettings(): Array<WidgetSettings>
}