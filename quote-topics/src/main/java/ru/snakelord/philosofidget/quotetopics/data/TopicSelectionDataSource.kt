package ru.snakelord.philosofidget.quotetopics.data

import android.content.SharedPreferences

interface TopicSelectionDataSource {
    fun getSelectedTopicId(): String?
    fun setSelectedTopicId(id: String)
}

class PreferencesTopicSelectionDataSource(
    private val preferences: SharedPreferences
) : TopicSelectionDataSource {
    override fun getSelectedTopicId(): String? = preferences.getString(SELECTED_TOPIC, null)
    override fun setSelectedTopicId(id: String) {
        preferences.edit().putString(SELECTED_TOPIC, id).apply()
    }

    private companion object {
        const val SELECTED_TOPIC = "selected_quote_topic"
    }
}
