package ru.snakelord.philosofidget.data.repository

import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsDataSource
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository

class WidgetSettingsRepositoryImpl(
    private val widgetSettingsDataSource: WidgetSettingsDataSource
) : WidgetSettingsRepository {
    override fun getAuthorVisibility(): Boolean = widgetSettingsDataSource.getAuthorVisibility()

    override fun setAuthorVisibility(isAuthorVisible: Boolean) = widgetSettingsDataSource.setAuthorVisibility(isAuthorVisible)

    override fun getWidgetSettings(): Array<WidgetSettings> = arrayOf(
        WidgetSettings.Toggle(
            titleRes = R.string.widget_settings_author_visibility_title,
            currentValue = getAuthorVisibility(),
            toggleTarget = WidgetSettings.Toggle.ToggleTarget.AUTHOR_VISIBILITY
        )
    )
}