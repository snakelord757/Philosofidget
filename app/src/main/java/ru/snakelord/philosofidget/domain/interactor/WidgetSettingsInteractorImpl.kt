package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository

class WidgetSettingsInteractorImpl(private val widgetSettingsRepository: WidgetSettingsRepository) : WidgetSettingsInteractor {
    override fun setAuthorVisibility(isAuthorVisible: Boolean) = widgetSettingsRepository.setAuthorVisibility(isAuthorVisible)

    override fun getWidgetSettings(): Array<WidgetSettings> = widgetSettingsRepository.getWidgetSettings()

    override fun getQuoteWidgetParams(): QuoteWidgetParams = QuoteWidgetParams(
        isAuthorVisible = widgetSettingsRepository.getAuthorVisibility()
    )
}