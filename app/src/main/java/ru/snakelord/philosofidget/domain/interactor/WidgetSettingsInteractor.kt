package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.TextGravity
import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsInteractor {
    suspend fun setNewWidgetParams(newWidgetParams: QuoteWidgetParams)

    suspend fun getWidgetSettings(): Array<WidgetSettings>

    suspend fun getQuoteWidgetParams(): QuoteWidgetParams

    suspend fun clearQuoteWidgetParams()

    fun resolveLanguage(language: String): Lang

    fun resolveGravity(gravity: String): TextGravity
}