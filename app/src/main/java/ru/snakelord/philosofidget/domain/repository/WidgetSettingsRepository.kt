package ru.snakelord.philosofidget.domain.repository

import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsRepository {
    fun getAuthorVisibility(): Boolean

    fun setAuthorVisibility(isAuthorVisible: Boolean)

    fun getQuoteLanguage(): String

    fun setQuoteLanguage(language: String)

    fun setQuoteTextSize(quoteTextSize: Float)

    fun getQuoteTextSize(): Float

    fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float)

    fun getQuoteAuthorTextSize(): Float

    fun getWidgetSettings(): Array<WidgetSettings>
}