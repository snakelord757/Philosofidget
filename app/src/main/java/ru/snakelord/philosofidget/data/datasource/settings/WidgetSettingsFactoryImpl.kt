package ru.snakelord.philosofidget.data.datasource.settings

import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.factory.WidgetSettingsFactory
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver

class WidgetSettingsFactoryImpl(
    private val widgetSettingsDataSource: WidgetSettingsDataSource,
    private val stringResolver: StringResolver
) : WidgetSettingsFactory {
    override suspend fun getWidgetSettings(): Array<WidgetSettings> {
        val gravityList = listOf(
            stringResolver.getString(R.string.widget_spinner_text_gravity_start),
            stringResolver.getString(R.string.widget_spinner_text_gravity_end),
            stringResolver.getString(R.string.widget_spinner_text_gravity_center)
        )
        return arrayOf(
            WidgetSettings.Toggle(
                title = stringResolver.getString(R.string.widget_settings_author_visibility_title),
                currentValue = widgetSettingsDataSource.getAuthorVisibility(),
                toggleTarget = WidgetSettings.Toggle.ToggleTarget.AUTHOR_VISIBILITY
            ),
            WidgetSettings.Spinner(
                title = stringResolver.getString(R.string.widget_settings_quote_language),
                currentVariant = widgetSettingsDataSource.getQuoteLanguage(),
                variants = listOf(
                    stringResolver.getString(R.string.widget_spinner_lang_russian),
                    stringResolver.getString(R.string.widget_spinner_lang_english)
                ),
                spinnerTarget = WidgetSettings.Spinner.SpinnerTarget.LANGUAGE
            ),
            WidgetSettings.Spinner(
                title = stringResolver.getString(R.string.widget_settings_quote_text_gravity),
                currentVariant = widgetSettingsDataSource.getQuoteTextGravity(),
                variants = gravityList,
                spinnerTarget = WidgetSettings.Spinner.SpinnerTarget.QUOTE_TEXT_GRAVITY
            ),
            WidgetSettings.Spinner(
                title = stringResolver.getString(R.string.widget_settings_quote_author_text_gravity),
                currentVariant = widgetSettingsDataSource.getQuoteAuthorTextGravity(),
                variants = gravityList,
                spinnerTarget = WidgetSettings.Spinner.SpinnerTarget.QUOTE_AUTHOR_GRAVITY
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
                sliderTarget = WidgetSettings.Slider.SliderTarget.QUOTE_UPDATE_TIME
            ),
            WidgetSettings.ColorPicker(
                title = stringResolver.getString(R.string.widget_settings_quote_text_color),
                currentColor = widgetSettingsDataSource.getQuoteTextColor(),
                colorPickerTarget = WidgetSettings.ColorPicker.ColorPickerTarget.QUOTE_TEXT_COLOR
            ),
            WidgetSettings.ColorPicker(
                title = stringResolver.getString(R.string.widget_settings_quote_author_text_color),
                currentColor = widgetSettingsDataSource.getQuoteAuthorTextColor(),
                colorPickerTarget = WidgetSettings.ColorPicker.ColorPickerTarget.QUOTE_AUTHOR_TEXT_COLOR
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