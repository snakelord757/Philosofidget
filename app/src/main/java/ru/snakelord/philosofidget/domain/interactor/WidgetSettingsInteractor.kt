package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings

interface WidgetSettingsInteractor {
    fun setAuthorVisibility(isAuthorVisible: Boolean)

    fun getWidgetSettings(): Array<WidgetSettings>

    fun getQuoteWidgetParams(): QuoteWidgetParams
}