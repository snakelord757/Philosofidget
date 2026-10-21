package ru.snakelord.philosofidget.quotetopics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.quotetopics.data.QuoteTopicsDataSource
import ru.snakelord.philosofidget.quotetopics.data.QuoteTopicsRepositoryImpl
import ru.snakelord.philosofidget.quotetopics.data.TopicSelectionDataSource
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopic
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsInteractor
import ru.snakelord.philosofidget.quotetopics.presentation.QuoteTopicsViewModel

class QuoteTopicsBehaviorTest {
    private val first = QuoteTopic("first", "First topic")
    private val second = QuoteTopic("second", "Second topic")

    private class SelectionSource(var id: String? = null) : TopicSelectionDataSource {
        val savedIds = mutableListOf<String>()
        var failure: RuntimeException? = null
        override fun getSelectedTopicId() = id
        override fun setSelectedTopicId(id: String) {
            failure?.let { throw it }
            this.id = id
            savedIds.add(id)
        }
    }

    private fun repository(
        storage: SelectionSource,
        topics: List<QuoteTopic> = listOf(first, second)
    ) = QuoteTopicsRepositoryImpl(object : QuoteTopicsDataSource {
        override fun getTopics() = topics
    }, storage)

    @Test fun `empty catalog cannot select or restore a topic`() {
        val storage = SelectionSource("first")
        val repository = repository(storage, emptyList())
        assertTrue(repository.topics.isEmpty())
        assertNull(repository.selectedTopic.value)
        assertFalse(repository.selectTopic("first"))
        assertEquals("first", storage.id)
        assertTrue(storage.savedIds.isEmpty())
    }

    @Test fun `view model exposes supplied catalog order and full restored topic`() {
        val storage = SelectionSource("second")
        val viewModel = QuoteTopicsViewModel(QuoteTopicsInteractor(repository(storage)))
        assertEquals(listOf(first, second), viewModel.topics)
        assertEquals(second, viewModel.selectedTopic.value)
        assertTrue(storage.savedIds.isEmpty())
    }

    @Test fun `selection replaces persisted topic and is visible in both view models`() {
        val storage = SelectionSource("first")
        val repository = repository(storage)
        val firstViewModel = QuoteTopicsViewModel(QuoteTopicsInteractor(repository))
        val secondViewModel = QuoteTopicsViewModel(QuoteTopicsInteractor(repository))
        assertTrue(firstViewModel.selectTopic("second"))
        assertEquals(second, firstViewModel.selectedTopic.value)
        assertEquals(second, secondViewModel.selectedTopic.value)
        assertEquals("second", storage.id)
        assertTrue(secondViewModel.selectTopic("first"))
        assertEquals(first, firstViewModel.selectedTopic.value)
        assertEquals(first, secondViewModel.selectedTopic.value)
        assertEquals(listOf("second", "first"), storage.savedIds)
    }

    @Test fun `reselecting current topic succeeds and retains its state`() {
        val storage = SelectionSource("first")
        val repository = repository(storage)
        assertTrue(repository.selectTopic("first"))
        assertEquals(first, repository.selectedTopic.value)
        assertEquals("first", storage.id)
    }

    @Test fun `subscriber receives initial selection and only actual selection changes`() = runBlocking {
        val viewModel = QuoteTopicsViewModel(QuoteTopicsInteractor(repository(SelectionSource("first"))))
        val observed = mutableListOf<QuoteTopic?>()
        // Unconfined collects each synchronous update immediately, without clocks or delays.
        val subscription = launch(Dispatchers.Unconfined) {
            viewModel.selectedTopic.collect { observed.add(it) }
        }
        try {
            assertEquals(listOf(first), observed)
            assertTrue(viewModel.selectTopic("second"))
            assertEquals(listOf(first, second), observed)
            assertFalse(viewModel.selectTopic("missing"))
            assertTrue(viewModel.selectTopic("second"))
            assertEquals(listOf(first, second), observed)
            assertTrue(viewModel.selectTopic("first"))
            assertEquals(listOf(first, second, first), observed)
        } finally {
            subscription.cancelAndJoin()
        }
    }

    @Test fun `invalid identifiers from view model do not change selection or storage`() {
        val storage = SelectionSource("first")
        val viewModel = QuoteTopicsViewModel(QuoteTopicsInteractor(repository(storage)))
        listOf("", " ", "FIRST", " first ", "missing").forEach { id ->
            assertFalse(id, viewModel.selectTopic(id))
            assertEquals(first, viewModel.selectedTopic.value)
            assertEquals("first", storage.id)
        }
        assertTrue(storage.savedIds.isEmpty())
    }

    @Test fun `valid confirmation replaces an obsolete persisted identifier`() {
        val storage = SelectionSource("obsolete")
        val repository = repository(storage)
        assertNull(repository.selectedTopic.value)
        assertTrue(repository.selectTopic("second"))
        assertEquals(second, repository.selectedTopic.value)
        assertEquals("second", storage.id)
        assertEquals(listOf("second"), storage.savedIds)
    }

    @Test fun `failed persistence does not publish unpersisted selection and permits retry`() {
        val storage = SelectionSource("first")
        val viewModel = QuoteTopicsViewModel(QuoteTopicsInteractor(repository(storage)))
        val failure = IllegalStateException("Storage unavailable")
        storage.failure = failure
        val actual = assertThrows(IllegalStateException::class.java) {
            viewModel.selectTopic("second")
        }
        assertSame(failure, actual)
        assertEquals(first, viewModel.selectedTopic.value)
        assertEquals("first", storage.id)
        assertTrue(storage.savedIds.isEmpty())
        storage.failure = null
        assertTrue(viewModel.selectTopic("second"))
        assertEquals(second, viewModel.selectedTopic.value)
        assertEquals(listOf("second"), storage.savedIds)
    }
}
