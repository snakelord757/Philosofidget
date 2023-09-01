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
    override suspend fun setAuthorVisibility(isAuthorVisible: Boolean) = widgetSettingsRepository.setAuthorVisibility(isAuthorVisible)

    override suspend fun setQuoteLanguage(language: String) = widgetSettingsRepository.setQuoteLanguage(language)

    override suspend fun setQuoteTextSize(quoteTextSize: Float) = widgetSettingsRepository.setQuoteTextSize(quoteTextSize)

    override suspend fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float) = widgetSettingsRepository.setQuoteAuthorTextSize(quoteAuthorTextSize)

    override suspend fun setWidgetUpdateTime(updateTime: Long) = widgetSettingsRepository.setWidgetUpdateTime(updateTime)

    override suspend fun getWidgetSettings(): Array<WidgetSettings> = widgetSettingsRepository.getWidgetSettings()

    override suspend fun getQuoteWidgetParams(): QuoteWidgetParams {
        val languagesMap = listOf(
            stringResolver.getString(R.string.widget_spinner_lang_russian),
            stringResolver.getString(R.string.widget_spinner_lang_english)
        )
            .zip(Lang.values())
            .toMap()
        return QuoteWidgetParams(
            isAuthorVisible = widgetSettingsRepository.getAuthorVisibility(),
            quoteLang = languagesMap.getOrDefault(widgetSettingsRepository.getQuoteLanguage(), Lang.RU),
            quoteTextSize = widgetSettingsRepository.getQuoteTextSize(),
            quoteAuthorTextSize = widgetSettingsRepository.getQuoteAuthorTextSize()
        )
    }
}