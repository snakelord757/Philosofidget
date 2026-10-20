package ru.snakelord.philosofidget.quotetopics.domain

class QuoteTopicsInteractor(private val repository: QuoteTopicsRepository) {
    val topics get() = repository.topics
    val selectedTopic get() = repository.selectedTopic
    fun selectTopic(id: String) = repository.selectTopic(id)
}
