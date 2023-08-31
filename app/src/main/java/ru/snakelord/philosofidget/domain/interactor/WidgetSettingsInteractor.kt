package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsInteractor {
    suspend fun setAuthorVisibility(isAuthorVisible: Boolean)

    suspend fun setQuoteLanguage(language: String)

    suspend fun setQuoteTextSize(quoteTextSize: Float)

    suspend fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float)

    suspend fun setWidgetUpdateTime(updateTime: Long)

    suspend fun getWidgetSettings(): Array<WidgetSettings>

    suspend fun getQuoteWidgetParams(): QuoteWidgetParams
}