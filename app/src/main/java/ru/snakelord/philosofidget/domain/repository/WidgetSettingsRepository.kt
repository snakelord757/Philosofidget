package ru.snakelord.philosofidget.domain.repository

import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsRepository {
    fun getAuthorVisibility(): Boolean

    fun setAuthorVisibility(isAuthorVisible: Boolean)

    fun getQuoteLanguage(): String

    fun setQuoteLanguage(language: String)

    fun getWidgetSettings(): Array<WidgetSettings>
}