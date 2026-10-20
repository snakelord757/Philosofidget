package ru.snakelord.philosofidget.quotetopics.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsRepository

class QuoteTopicsRepositoryImpl(
    topicsDataSource: QuoteTopicsDataSource,
    private val selectionDataSource: TopicSelectionDataSource
) : QuoteTopicsRepository {
    override val topics = topicsDataSource.getTopics()
    private val selection = MutableStateFlow(
        topics.firstOrNull { it.id == selectionDataSource.getSelectedTopicId() }
    )
    override val selectedTopic = selection.asStateFlow()

    override fun selectTopic(id: String): Boolean {
        val topic = topics.firstOrNull { it.id == id } ?: return false
        selectionDataSource.setSelectedTopicId(topic.id)
        selection.value = topic
        return true
    }
}
