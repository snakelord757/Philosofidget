package ru.snakelord.philosofidget.domain.repository

import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsRepository {
    fun getAuthorVisibility(): Boolean

    fun setAuthorVisibility(isAuthorVisible: Boolean)

    fun getQuoteLanguage(): String

    fun setQuoteLanguage(language: String)

    fun setQuoteTextSize(quoteTextSize: Int)

    fun getQuoteTextSize(): Int

    fun setQuoteAuthorTextSize(quoteAuthorTextSize: Int)

    fun getQuoteAuthorTextSize(): Int

    fun getWidgetSettings(): Array<WidgetSettings>
}