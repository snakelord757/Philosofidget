package ru.snakelord.philosofidget.quotetopics.domain

import kotlinx.coroutines.flow.StateFlow

interface QuoteTopicsRepository {
    val topics: List<QuoteTopic>
    val selectedTopic: StateFlow<QuoteTopic?>
    fun selectTopic(id: String): Boolean
}
