package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver

class WidgetSettingsInteractorImpl(
    private val widgetSettingsRepository: WidgetSettingsRepository,
    private val stringResolver: StringResolver
) : WidgetSettingsInteractor {
    override fun setAuthorVisibility(isAuthorVisible: Boolean) = widgetSettingsRepository.setAuthorVisibility(isAuthorVisible)

    override fun setQuoteLanguage(language: String) = widgetSettingsRepository.setQuoteLanguage(language)

    override fun getWidgetSettings(): Array<WidgetSettings> = widgetSettingsRepository.getWidgetSettings()

    override fun getQuoteWidgetParams(): QuoteWidgetParams {
        val languagesMap = listOf(
            stringResolver.getString(R.string.widget_spinner_lang_russian),
            stringResolver.getString(R.string.widget_spinner_lang_english)
        )
            .zip(Lang.values())
            .toMap()
        return QuoteWidgetParams(
            isAuthorVisible = widgetSettingsRepository.getAuthorVisibility(),
            quoteLang = languagesMap.getOrDefault(widgetSettingsRepository.getQuoteLanguage(), Lang.RU)
        )
    }
}