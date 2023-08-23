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
        settingsPreferences.getString(PREFERENCES_QUOTE_LANGUAGE_KEY, PREFERENCE_QUOTE_LANGUAGE_DEFAULT) ?: PREFERENCE_QUOTE_LANGUAGE_DEFAULT

    override fun setQuoteLanguage(language: String) {
        settingsPreferences.edit { putString(PREFERENCES_QUOTE_LANGUAGE_KEY, language) }
    }

    override fun setQuoteTextSize(quoteTextSize: Float) {
        settingsPreferences.edit { putFloat(PREFERENCES_QUOTE_TEXT_SIZE_KEY, quoteTextSize) }
    }

    override fun getQuoteTextSize(): Float = settingsPreferences.getFloat(PREFERENCES_QUOTE_TEXT_SIZE_KEY, PREFERENCE_QUOTE_TEXT_SIZE_DEFAULT)

    override fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float) {
        settingsPreferences.edit { putFloat(PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY, quoteAuthorTextSize) }
    }

    override fun getQuoteAuthorTextSize(): Float =
        settingsPreferences.getFloat(PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY, PREFERENCE_QUOTE_AUTHOR_TEXT_SIZE_DEFAULT)

    private companion object {
        const val PREFERENCES_IS_AUTHOR_VISIBLE_KEY = "PREFERENCES_IS_AUTHOR_VISIBLE_KEY"
        const val PREFERENCES_QUOTE_LANGUAGE_KEY = "PREFERENCES_QUOTE_LANGUAGE_KEY"
        const val PREFERENCES_QUOTE_TEXT_SIZE_KEY = "PREFERENCES_QUOTE_TEXT_SIZE_KEY"
        const val PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY = "PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY"

        const val PREFERENCE_IS_AUTHOR_VISIBLE_DEFAULT = true
        const val PREFERENCE_QUOTE_LANGUAGE_DEFAULT = "Русский"
        const val PREFERENCE_QUOTE_TEXT_SIZE_DEFAULT = 30f
        const val PREFERENCE_QUOTE_AUTHOR_TEXT_SIZE_DEFAULT = 15f
    }
}