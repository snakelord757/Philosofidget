package ru.snakelord.philosofidget.quotetopics.data

import android.content.Context
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopic
import ru.snakelord.philosofidget.quotetopics.domain.TopicsRepository

class QuoteTopicsRepository(private val store: TopicSelectionStore) : TopicsRepository {
    constructor(context: Context) : this(PreferencesTopicSelectionStore(context.applicationContext))

    override fun topics(): List<QuoteTopic> = MOCK_TOPICS.toList()

    override fun selectedTopic(): QuoteTopic? = MOCK_TOPICS.find { it.id == store.selectedId() }

    override fun selectTopic(id: String) {
        require(MOCK_TOPICS.any { it.id == id }) { "Unknown quote topic: $id" }
        store.saveSelectedId(id)
    }

    private companion object {
        val MOCK_TOPICS = listOf(
            QuoteTopic("wisdom", "Мудрость"),
            QuoteTopic("life", "Жизнь"),
            QuoteTopic("love", "Любовь"),
            QuoteTopic("friendship", "Дружба"),
            QuoteTopic("happiness", "Счастье"),
            QuoteTopic("success", "Успех"),
            QuoteTopic("motivation", "Мотивация"),
            QuoteTopic("dreams", "Мечты"),
            QuoteTopic("time", "Время"),
            QuoteTopic("freedom", "Свобода"),
            QuoteTopic("courage", "Смелость"),
            QuoteTopic("kindness", "Доброта"),
            QuoteTopic("knowledge", "Знания"),
            QuoteTopic("creativity", "Творчество"),
            QuoteTopic("nature", "Природа"),
            QuoteTopic("hope", "Надежда"),
            QuoteTopic("change", "Перемены"),
            QuoteTopic("self_discovery", "Самопознание")
        )
    }
}

private class PreferencesTopicSelectionStore(context: Context) : TopicSelectionStore {
    private val preferences = context.getSharedPreferences("QUOTE_TOPICS_PREFS", Context.MODE_PRIVATE)

    override fun selectedId(): String? = preferences.getString("selected_topic_id", null)

    override fun saveSelectedId(id: String) {
        preferences.edit().putString("selected_topic_id", id).apply()
    }
}
