package ru.snakelord.philosofidget.data.datasource.settings

import android.content.SharedPreferences
import androidx.core.content.edit

class WidgetSettingsDataSourceImpl(
    private val settingsPreferences: SharedPreferences
) : WidgetSettingsDataSource {
    override fun getAuthorVisibility(): Boolean = settingsPreferences.getBoolean(PREFERENCES_IS_AUTHOR_VISIBLE_KEY, true)

    override fun setAuthorVisibility(isAuthorVisible: Boolean) {
        settingsPreferences.edit { putBoolean(PREFERENCES_IS_AUTHOR_VISIBLE_KEY, isAuthorVisible) }
    }

    private companion object {
        const val PREFERENCES_IS_AUTHOR_VISIBLE_KEY = "PREFERENCES_IS_AUTHOR_VISIBLE_KEY"
    }
}