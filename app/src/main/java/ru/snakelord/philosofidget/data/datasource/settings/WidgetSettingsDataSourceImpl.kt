package ru.snakelord.philosofidget.data.datasource.settings

import android.content.SharedPreferences
import androidx.core.content.edit

class WidgetSettingsDataSourceImpl(
    private val settingsPreferences: SharedPreferences
) : WidgetSettingsDataSource {
    override fun getAuthorVisibility(): Boolean =
        settingsPreferences.getBoolean(PREFERENCES_IS_AUTHOR_VISIBLE_KEY, PREFERENCE_IS_AUTHOR_VISIBLE_DEFAULT)

    override fun setAuthorVisibility(isAuthorVisible: Boolean) {
        settingsPreferences.edit { putBoolean(PREFERENCES_IS_AUTHOR_VISIBLE_KEY, isAuthorVisible) }
    }

    override fun getQuoteLanguage(): String =
        settingsPreferences.getString(PREFERENCE_QUOTE_LANGUAGE_KEY, PREFERENCE_QUOTE_LANGUAGE_DEFAULT) ?: PREFERENCE_QUOTE_LANGUAGE_DEFAULT

    override fun setQuoteLanguage(language: String) {
        settingsPreferences.edit { putString(PREFERENCE_QUOTE_LANGUAGE_KEY, language) }
    }

    private companion object {
        const val PREFERENCES_IS_AUTHOR_VISIBLE_KEY = "PREFERENCES_IS_AUTHOR_VISIBLE_KEY"
        const val PREFERENCE_QUOTE_LANGUAGE_KEY = "PREFERENCE_QUOTE_LANGUAGE_KEY"

        const val PREFERENCE_IS_AUTHOR_VISIBLE_DEFAULT = true
        const val PREFERENCE_QUOTE_LANGUAGE_DEFAULT = "Русский"
    }
}