package ru.snakelord.philosofidget.data.repository

import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsDataSource
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver

class WidgetSettingsRepositoryImpl(
    private val widgetSettingsDataSource: WidgetSettingsDataSource,
    private val stringResolver: StringResolver
) : WidgetSettingsRepository {
    override fun getAuthorVisibility(): Boolean = widgetSettingsDataSource.getAuthorVisibility()

    override fun setAuthorVisibility(isAuthorVisible: Boolean) = widgetSettingsDataSource.setAuthorVisibility(isAuthorVisible)

    override fun getQuoteLanguage(): String = widgetSettingsDataSource.getQuoteLanguage()

    override fun setQuoteLanguage(language: String) = widgetSettingsDataSource.setQuoteLanguage(language)

    override fun setQuoteTextSize(quoteTextSize: Float) = widgetSettingsDataSource.setQuoteTextSize(quoteTextSize)

    override fun getQuoteTextSize(): Float = widgetSettingsDataSource.getQuoteTextSize()

    override fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float) = widgetSettingsDataSource.setQuoteAuthorTextSize(quoteAuthorTextSize)

    override fun getQuoteAuthorTextSize(): Float = widgetSettingsDataSource.getQuoteAuthorTextSize()

    override fun getWidgetSettings(): Array<WidgetSettings> {
        return arrayOf(
            WidgetSettings.Toggle(
                title = stringResolver.getString(R.string.widget_settings_author_visibility_title),
                currentValue = getAuthorVisibility(),
                toggleTarget = WidgetSettings.Toggle.ToggleTarget.AUTHOR_VISIBILITY
            ),
            WidgetSettings.Spinner(
                title = stringResolver.getString(R.string.widget_settings_quote_language),
                currentVariant = widgetSettingsDataSource.getQuoteLanguage(),
                variants = listOf(
                    stringResolver.getString(R.string.widget_spinner_lang_russian),
                    stringResolver.getString(R.string.widget_spinner_lang_english)
                )
            ),
            WidgetSettings.SeekBar(
                title = stringResolver.getString(R.string.widget_settings_quote_text_size),
                currentValue = widgetSettingsDataSource.getQuoteTextSize(),
                minValue = MIN_TEXT_SIZE,
                maxValue = QUOTE_MAX_TEXT_SIZE,
                seekBarTarget = WidgetSettings.SeekBar.SeekBarTarget.QUOTE_TEXT_SIZE
            ),
            WidgetSettings.SeekBar(
                title = stringResolver.getString(R.string.widget_settings_quote_author_text_size),
                currentValue = widgetSettingsDataSource.getQuoteAuthorTextSize(),
                minValue = MIN_TEXT_SIZE,
                maxValue = QUOTE_AUTHOR_MAX_TEXT_SIZE,
                seekBarTarget = WidgetSettings.SeekBar.SeekBarTarget.QUOTE_AUTHOR_TEXT_SIZE
            )
        )
    }

    private companion object {
        // The lowest possible text size value
        const val MIN_TEXT_SIZE = 11f
        const val QUOTE_MAX_TEXT_SIZE = 30f
        const val QUOTE_AUTHOR_MAX_TEXT_SIZE = 15f
    }
}