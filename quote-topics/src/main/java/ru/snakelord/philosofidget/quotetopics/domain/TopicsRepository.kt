package ru.snakelord.philosofidget.quotetopics.domain

interface TopicsRepository {
    fun topics(): List<QuoteTopic>
    fun selectedTopic(): QuoteTopic?
    fun selectTopic(id: String)
}
