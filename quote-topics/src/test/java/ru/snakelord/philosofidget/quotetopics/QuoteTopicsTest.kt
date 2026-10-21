package ru.snakelord.philosofidget.quotetopics

import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.quotetopics.data.*
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsInteractor
import ru.snakelord.philosofidget.quotetopics.presentation.QuoteTopicsViewModel

class QuoteTopicsTest {
    private class SelectionSource(var id: String? = null) : TopicSelectionDataSource {
        var writes = 0
        override fun getSelectedTopicId() = id
        override fun setSelectedTopicId(id: String) { this.id = id; writes++ }
    }

    @Test fun `mock topics contain eighteen distinct populated entries`() {
        val topics = MockQuoteTopicsDataSource().getTopics()
        assertEquals(18, topics.size)
        assertEquals(topics.size, topics.map { it.id }.distinct().size)
        assertTrue(topics.all { it.id.isNotBlank() && it.title.isNotBlank() })
    }

    @Test fun `opening topics preserves empty selection until confirmation`() {
        val storage = SelectionSource()
        val viewModel = QuoteTopicsViewModel(QuoteTopicsInteractor(
            QuoteTopicsRepositoryImpl(MockQuoteTopicsDataSource(), storage)
        ))
        assertEquals(18, viewModel.topics.size)
        assertNull(viewModel.selectedTopic.value)
        assertEquals(0, storage.writes)
        assertTrue(viewModel.selectTopic("wisdom"))
        assertEquals("wisdom", viewModel.selectedTopic.value?.id)
        assertEquals("wisdom", storage.id)
        assertEquals(1, storage.writes)
    }

    @Test fun `repository restores persisted selection`() {
        val storage = SelectionSource("love")
        val repository = QuoteTopicsRepositoryImpl(MockQuoteTopicsDataSource(), storage)
        assertEquals("love", repository.selectedTopic.value?.id)
        assertEquals(0, storage.writes)
    }

    @Test fun `invalid selection preserves current topic and storage`() {
        val storage = SelectionSource("life")
        val interactor = QuoteTopicsInteractor(QuoteTopicsRepositoryImpl(MockQuoteTopicsDataSource(), storage))
        assertFalse(interactor.selectTopic("unknown"))
        assertEquals("life", interactor.selectedTopic.value?.id)
        assertEquals("life", storage.id)
        assertEquals(0, storage.writes)
    }

    @Test fun `unknown persisted topic displays no selection`() {
        val repository = QuoteTopicsRepositoryImpl(MockQuoteTopicsDataSource(), SelectionSource("unknown"))
        assertNull(repository.selectedTopic.value)
    }

    @Test fun `selection survives repository recreation`() {
        val storage = SelectionSource()
        val first = QuoteTopicsRepositoryImpl(MockQuoteTopicsDataSource(), storage)
        first.selectTopic("peace")
        val restored = QuoteTopicsRepositoryImpl(MockQuoteTopicsDataSource(), storage)
        assertEquals(first.selectedTopic.value, restored.selectedTopic.value)
    }
}
