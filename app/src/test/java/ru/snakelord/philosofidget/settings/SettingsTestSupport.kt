package ru.snakelord.philosofidget.settings

import android.content.SharedPreferences
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsDataSourceImpl
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsFactoryImpl
import ru.snakelord.philosofidget.data.repository.WidgetSettingsRepositoryImpl
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractorImpl
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver

internal class SettingsFixture {
    val preferences = SettingsPreferences()
    val strings = object : StringResolver {
        override fun getString(stringResId: Int): String = when (stringResId) {
            R.string.widget_spinner_lang_russian -> "Russian"
            R.string.widget_spinner_lang_english -> "English"
            R.string.widget_spinner_text_gravity_start -> "Start"
            R.string.widget_spinner_text_gravity_end -> "End"
            R.string.widget_spinner_text_gravity_center -> "Center"
            else -> "title:$stringResId"
        }
    }
    val source = WidgetSettingsDataSourceImpl(preferences, strings)
    val factory = WidgetSettingsFactoryImpl(source, strings)
    val repository = WidgetSettingsRepositoryImpl(source, factory)
    val interactor = WidgetSettingsInteractorImpl(repository, strings)
}

/** In-memory implementation of the storage boundary, without Android runtime calls. */
internal class SettingsPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any?>()
    var failure: RuntimeException? = null
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String?, defValue: String?): String? = values[key] as String? ?: defValue
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
        throw UnsupportedOperationException("String sets are not used by settings")
    override fun getInt(key: String?, defValue: Int): Int = values[key] as Int? ?: defValue
    override fun getLong(key: String?, defValue: Long): Long {
        failure?.let { throw it }
        return values[key] as Long? ?: defValue
    }
    override fun getFloat(key: String?, defValue: Float): Float = values[key] as Float? ?: defValue
    override fun getBoolean(key: String?, defValue: Boolean): Boolean = values[key] as Boolean? ?: defValue
    override fun contains(key: String?): Boolean = values.containsKey(key)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) = Unit
    override fun edit(): SharedPreferences.Editor {
        failure?.let { throw it }
        return object : SharedPreferences.Editor {
            private val pending = mutableMapOf<String, Any?>()
            private var clearRequested = false
            override fun putString(key: String?, value: String?): SharedPreferences.Editor = apply { pending[requireNotNull(key)] = value }
            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor =
                throw UnsupportedOperationException("String sets are not used by settings")
            override fun putInt(key: String?, value: Int): SharedPreferences.Editor = apply { pending[requireNotNull(key)] = value }
            override fun putLong(key: String?, value: Long): SharedPreferences.Editor = apply { pending[requireNotNull(key)] = value }
            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = apply { pending[requireNotNull(key)] = value }
            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = apply { pending[requireNotNull(key)] = value }
            override fun remove(key: String?): SharedPreferences.Editor = apply { pending[requireNotNull(key)] = null }
            override fun clear(): SharedPreferences.Editor = apply { clearRequested = true }
            override fun commit(): Boolean {
                if (clearRequested) values.clear()
                pending.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
                return true
            }
            override fun apply() { commit() }
        }
    }
}
