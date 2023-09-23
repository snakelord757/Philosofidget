package ru.snakelord.philosofidget.domain.interactor

import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.TextGravity
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

    private val textGravityMap = listOf(
        stringResolver.getString(R.string.widget_spinner_text_gravity_start),
        stringResolver.getString(R.string.widget_spinner_text_gravity_end),
        stringResolver.getString(R.string.widget_spinner_text_gravity_center)
    )
        .zip(TextGravity.values())
        .toMap()

    override suspend fun setNewWidgetParams(newWidgetParams: QuoteWidgetParams) = with(newWidgetParams) {
        widgetSettingsRepository.setQuoteTextSize(quoteTextSize)
        widgetSettingsRepository.setQuoteAuthorTextSize(quoteAuthorTextSize)
        widgetSettingsRepository.setAuthorVisibility(isAuthorVisible)
        widgetSettingsRepository.setQuoteLanguage(languagesMap.findKey(newWidgetParams.quoteLang))
        widgetSettingsRepository.setWidgetUpdateTime(quoteUpdateTime)
        widgetSettingsRepository.setQuoteTextGravity(textGravityMap.findKey(newWidgetParams.quoteTextGravity))
        widgetSettingsRepository.setQuoteAuthorTextGravity(textGravityMap.findKey(newWidgetParams.quoteAuthorTextGravity))
    }

    override suspend fun getWidgetSettings(): Array<WidgetSettings> = widgetSettingsRepository.getWidgetSettings()

    override suspend fun getQuoteWidgetParams(): QuoteWidgetParams {
        return QuoteWidgetParams(
            isAuthorVisible = widgetSettingsRepository.getAuthorVisibility(),
            quoteLang = resolveLanguage(widgetSettingsRepository.getQuoteLanguage()),
            quoteTextSize = widgetSettingsRepository.getQuoteTextSize(),
            quoteAuthorTextSize = widgetSettingsRepository.getQuoteAuthorTextSize(),
            quoteUpdateTime = widgetSettingsRepository.getWidgetUpdateTime(),
            quoteTextGravity = resolveGravity(widgetSettingsRepository.getQuoteTextGravity()),
            quoteAuthorTextGravity = resolveGravity(widgetSettingsRepository.getQuoteAuthorTextGravity())
        )
    }

    override suspend fun clearQuoteWidgetParams() {
        widgetSettingsRepository.clearWidgetSettingsParams()
    }

    override fun resolveLanguage(language: String): Lang = languagesMap.getOrDefault(language, Lang.RU)

    override fun resolveGravity(gravity: String): TextGravity = textGravityMap.getOrDefault(gravity, TextGravity.START)

    private fun <K, V> Map<K, V>.findKey(value: V): K {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value == value) return entry.key
        }
        error("Unable to find key for value $value")
    }
}