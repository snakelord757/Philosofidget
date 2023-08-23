package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsInteractor {
    fun setAuthorVisibility(isAuthorVisible: Boolean)

    fun setQuoteLanguage(language: String)

    fun setQuoteTextSize(quoteTextSize: Float)

    fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float)

    fun getWidgetSettings(): Array<WidgetSettings>

    fun getQuoteWidgetParams(): QuoteWidgetParams
}