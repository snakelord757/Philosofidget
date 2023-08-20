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
            )
        )
    }
}