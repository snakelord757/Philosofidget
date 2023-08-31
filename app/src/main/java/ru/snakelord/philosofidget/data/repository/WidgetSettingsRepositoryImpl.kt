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
    override suspend fun getAuthorVisibility(): Boolean = widgetSettingsDataSource.getAuthorVisibility()

    override suspend fun setAuthorVisibility(isAuthorVisible: Boolean) = widgetSettingsDataSource.setAuthorVisibility(isAuthorVisible)

    override suspend fun getQuoteLanguage(): String = widgetSettingsDataSource.getQuoteLanguage()

    override suspend fun setQuoteLanguage(language: String) = widgetSettingsDataSource.setQuoteLanguage(language)

    override suspend fun setQuoteTextSize(quoteTextSize: Float) = widgetSettingsDataSource.setQuoteTextSize(quoteTextSize)

    override suspend fun getQuoteTextSize(): Float = widgetSettingsDataSource.getQuoteTextSize()

    override suspend fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float) = widgetSettingsDataSource.setQuoteAuthorTextSize(quoteAuthorTextSize)

    override suspend fun getQuoteAuthorTextSize(): Float = widgetSettingsDataSource.getQuoteAuthorTextSize()

    override suspend fun setWidgetUpdateTime(updateTime: Long) = widgetSettingsDataSource.setWidgetUpdateTime(updateTime)

    override suspend fun getWidgetUpdateTime(): Long = widgetSettingsDataSource.getWidgetUpdateTime()

    override suspend fun getWidgetSettings(): Array<WidgetSettings> {
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
            WidgetSettings.Slider(
                title = stringResolver.getString(R.string.widget_settings_quote_text_size),
                currentValue = widgetSettingsDataSource.getQuoteTextSize(),
                minValue = MIN_TEXT_SIZE,
                maxValue = QUOTE_MAX_TEXT_SIZE,
                sliderTarget = WidgetSettings.Slider.SliderTarget.QUOTE_TEXT_SIZE
            ),
            WidgetSettings.Slider(
                title = stringResolver.getString(R.string.widget_settings_quote_author_text_size),
                currentValue = widgetSettingsDataSource.getQuoteAuthorTextSize(),
                minValue = MIN_TEXT_SIZE,
                maxValue = QUOTE_AUTHOR_MAX_TEXT_SIZE,
                sliderTarget = WidgetSettings.Slider.SliderTarget.QUOTE_AUTHOR_TEXT_SIZE
            ),
            WidgetSettings.Slider(
                title = stringResolver.getString(R.string.widget_settings_update_time),
                currentValue = widgetSettingsDataSource.getWidgetUpdateTime().toFloat(),
                minValue = QUOTE_UPDATE_TIME_MIN_HOUR,
                maxValue = QUOTE_UPDATE_TIME_MAX_HOUR,
                formatToInt = true,
                sliderTarget = WidgetSettings.Slider.SliderTarget.QUOTE_UPDATE_TIME
            )
        )
    }

    private companion object {
        // The lowest possible text size value
        const val MIN_TEXT_SIZE = 11f
        const val QUOTE_MAX_TEXT_SIZE = 30f
        const val QUOTE_AUTHOR_MAX_TEXT_SIZE = 15f
        const val QUOTE_UPDATE_TIME_MIN_HOUR = 1f
        const val QUOTE_UPDATE_TIME_MAX_HOUR = 24f
    }
}