package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsInteractor {
    suspend fun setNewWidgetParams(newWidgetParams: QuoteWidgetParams)

    suspend fun getWidgetSettings(): Array<WidgetSettings>

    suspend fun getQuoteWidgetParams(): QuoteWidgetParams

    fun resolveLanguage(language: String): Lang
}