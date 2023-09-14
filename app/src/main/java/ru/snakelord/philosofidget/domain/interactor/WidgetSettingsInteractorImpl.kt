package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver

class WidgetSettingsInteractorImpl(
    private val widgetSettingsRepository: WidgetSettingsRepository,
    stringResolver: StringResolver
) : WidgetSettingsInteractor {

    private val languagesMap = listOf(
        stringResolver.getString(R.string.widget_spinner_lang_russian),
        stringResolver.getString(R.string.widget_spinner_lang_english)
    )
        .zip(Lang.values())
        .toMap()

    override suspend fun setNewWidgetParams(newWidgetParams: QuoteWidgetParams) = with(newWidgetParams) {
        widgetSettingsRepository.setQuoteTextSize(quoteTextSize)
        widgetSettingsRepository.setQuoteAuthorTextSize(quoteAuthorTextSize)
        widgetSettingsRepository.setAuthorVisibility(isAuthorVisible)
        widgetSettingsRepository.setQuoteLanguage(resolveLanguage(newWidgetParams.quoteLang))
        widgetSettingsRepository.setWidgetUpdateTime(quoteUpdateTime)
    }

    override suspend fun getWidgetSettings(): Array<WidgetSettings> = widgetSettingsRepository.getWidgetSettings()

    override suspend fun getQuoteWidgetParams(): QuoteWidgetParams {
        return QuoteWidgetParams(
            isAuthorVisible = widgetSettingsRepository.getAuthorVisibility(),
            quoteLang = resolveLanguage(widgetSettingsRepository.getQuoteLanguage()),
            quoteTextSize = widgetSettingsRepository.getQuoteTextSize(),
            quoteAuthorTextSize = widgetSettingsRepository.getQuoteAuthorTextSize(),
            quoteUpdateTime = widgetSettingsRepository.getWidgetUpdateTime()
        )
    }

    override fun resolveLanguage(language: String): Lang = languagesMap.getOrDefault(language, Lang.RU)

    private fun resolveLanguage(selectedLang: Lang): String {
        languagesMap.forEach { (key, value) ->
            if (value == selectedLang) return key
        }
        error("Unable to find $selectedLang")
    }
}