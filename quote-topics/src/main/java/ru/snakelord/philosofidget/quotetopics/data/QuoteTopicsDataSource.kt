package ru.snakelord.philosofidget.quotetopics.data

import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopic

interface QuoteTopicsDataSource {
    fun getTopics(): List<QuoteTopic>
}

class MockQuoteTopicsDataSource : QuoteTopicsDataSource {
    override fun getTopics() = listOf(
        QuoteTopic("life", "Жизнь"),
        QuoteTopic("wisdom", "Мудрость"),
        QuoteTopic("love", "Любовь"),
        QuoteTopic("friendship", "Дружба"),
        QuoteTopic("happiness", "Счастье"),
        QuoteTopic("inspiration", "Вдохновение"),
        QuoteTopic("motivation", "Мотивация"),
        QuoteTopic("success", "Успех"),
        QuoteTopic("dreams", "Мечты"),
        QuoteTopic("time", "Время"),
        QuoteTopic("freedom", "Свобода"),
        QuoteTopic("courage", "Смелость"),
        QuoteTopic("kindness", "Доброта"),
        QuoteTopic("knowledge", "Знания"),
        QuoteTopic("creativity", "Творчество"),
        QuoteTopic("nature", "Природа"),
        QuoteTopic("family", "Семья"),
        QuoteTopic("peace", "Душевное спокойствие")
    )
}
