package ru.snakelord.philosofidget.data.datasource.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver

class WidgetSettingsDataSourceImpl(
    private val settingsPreferences: SharedPreferences,
    private val stringResolver: StringResolver
) : WidgetSettingsDataSource {
    override suspend fun getAuthorVisibility(): Boolean =
        settingsPreferences.getBoolean(PREFERENCES_IS_AUTHOR_VISIBLE_KEY, PREFERENCE_IS_AUTHOR_VISIBLE_DEFAULT)

    override suspend fun setAuthorVisibility(isAuthorVisible: Boolean) =
        settingsPreferences.edit { putBoolean(PREFERENCES_IS_AUTHOR_VISIBLE_KEY, isAuthorVisible) }

    override suspend fun getQuoteLanguage(): String {
        val defaultValue = stringResolver.getString(R.string.widget_spinner_lang_russian)
        return settingsPreferences.getString(PREFERENCES_QUOTE_LANGUAGE_KEY, defaultValue) ?: defaultValue
    }

    override suspend fun setQuoteLanguage(language: String) = settingsPreferences.edit { putString(PREFERENCES_QUOTE_LANGUAGE_KEY, language) }

    override suspend fun setQuoteTextSize(quoteTextSize: Float) =
        settingsPreferences.edit { putFloat(PREFERENCES_QUOTE_TEXT_SIZE_KEY, quoteTextSize) }

    override suspend fun getQuoteTextSize(): Float = settingsPreferences.getFloat(PREFERENCES_QUOTE_TEXT_SIZE_KEY, PREFERENCE_QUOTE_TEXT_SIZE_DEFAULT)

    override suspend fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float) =
        settingsPreferences.edit { putFloat(PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY, quoteAuthorTextSize) }

    override suspend fun getQuoteAuthorTextSize(): Float =
        settingsPreferences.getFloat(PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY, PREFERENCE_QUOTE_AUTHOR_TEXT_SIZE_DEFAULT)

    override suspend fun setWidgetUpdateTime(updateTime: Long) = settingsPreferences.edit { putLong(PREFERENCES_WIDGET_UPDATE_TIME, updateTime) }

    override suspend fun getWidgetUpdateTime(): Long =
        settingsPreferences.getLong(PREFERENCES_WIDGET_UPDATE_TIME, PREFERENCE_WIDGET_UPDATE_TIME_DEFAULT)

    override suspend fun setQuoteTextGravity(gravity: String) = settingsPreferences.edit {
        putString(PREFERENCE_QUOTE_TEXT_GRAVITY, gravity)
    }

    override suspend fun getQuoteTextGravity(): String {
        val defaultValue = stringResolver.getString(R.string.widget_spinner_text_gravity_start)
        return settingsPreferences.getString(PREFERENCE_QUOTE_TEXT_GRAVITY, defaultValue) ?: defaultValue
    }

    override suspend fun setQuoteAuthorTextGravity(gravity: String) = settingsPreferences.edit {
        putString(PREFERENCE_QUOTE_AUTHOR_TEXT_GRAVITY, gravity)
    }

    override suspend fun getQuoteAuthorTextGravity(): String {
        val defaultValue = stringResolver.getString(R.string.widget_spinner_text_gravity_end)
        return settingsPreferences.getString(PREFERENCE_QUOTE_AUTHOR_TEXT_GRAVITY, defaultValue) ?: defaultValue
    }

    override suspend fun clearWidgetSettingsParams() {
        settingsPreferences.edit { clear() }
    }

    private companion object {
        const val PREFERENCES_IS_AUTHOR_VISIBLE_KEY = "PREFERENCES_IS_AUTHOR_VISIBLE_KEY"
        const val PREFERENCES_QUOTE_LANGUAGE_KEY = "PREFERENCES_QUOTE_LANGUAGE_KEY"
        const val PREFERENCES_QUOTE_TEXT_SIZE_KEY = "PREFERENCES_QUOTE_TEXT_SIZE_KEY"
        const val PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY = "PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY"
        const val PREFERENCES_WIDGET_UPDATE_TIME = "PREFERENCES_WIDGET_UPDATE_TIME"
        const val PREFERENCE_QUOTE_TEXT_GRAVITY = "PREFERENCE_QUOTE_TEXT_GRAVITY"
        const val PREFERENCE_QUOTE_AUTHOR_TEXT_GRAVITY = "PREFERENCE_QUOTE_AUTHOR_TEXT_GRAVITY"

        const val PREFERENCE_IS_AUTHOR_VISIBLE_DEFAULT = true
        const val PREFERENCE_QUOTE_TEXT_SIZE_DEFAULT = 30f
        const val PREFERENCE_QUOTE_AUTHOR_TEXT_SIZE_DEFAULT = 15f
        const val PREFERENCE_WIDGET_UPDATE_TIME_DEFAULT = 12L
    }
}