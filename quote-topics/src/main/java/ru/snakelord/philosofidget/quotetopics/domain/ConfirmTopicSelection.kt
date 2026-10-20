package ru.snakelord.philosofidget.quotetopics.domain

class ConfirmTopicSelection(private val repository: TopicsRepository) {
    operator fun invoke(topicId: String): Boolean {
        if (repository.topics().none { it.id == topicId }) return false
        repository.selectTopic(topicId)
        return true
    }
}
