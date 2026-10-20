package ru.snakelord.philosofidget.quotetopics

import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.quotetopics.data.QuoteTopicsRepository
import ru.snakelord.philosofidget.quotetopics.data.TopicSelectionStore
import ru.snakelord.philosofidget.quotetopics.domain.ConfirmTopicSelection

class QuoteTopicsRepositoryTest {
    @Test
    fun `catalog contains eighteen topics with unique stable ids and nonblank titles`() {
        val topics = QuoteTopicsRepository(MemoryStore()).topics()
        assertEquals(18, topics.size)
        assertEquals(topics.size, topics.map { it.id }.distinct().size)
        assertTrue(topics.all { it.id.isNotBlank() && it.title.isNotBlank() })
        assertEquals(topics, QuoteTopicsRepository(MemoryStore()).topics())
    }

    @Test
    fun `browsing candidates does not replace previous selection`() {
        val store = MemoryStore("life")
        val repository = QuoteTopicsRepository(store)
        repository.topics().first { it.id == "love" }
        assertEquals("life", repository.selectedTopic()?.id)
        assertEquals(0, store.writes)
    }

    @Test
    fun `confirmation persists selection across repository instances`() {
        val store = MemoryStore()
        assertTrue(ConfirmTopicSelection(QuoteTopicsRepository(store))("wisdom"))
        assertEquals("wisdom", QuoteTopicsRepository(store).selectedTopic()?.id)
        assertEquals(1, store.writes)
    }

    @Test
    fun `unknown confirmation preserves previous selection`() {
        val store = MemoryStore("love")
        assertFalse(ConfirmTopicSelection(QuoteTopicsRepository(store))("unknown"))
        assertEquals("love", store.selectedId())
        assertEquals(0, store.writes)
    }

    @Test
    fun `empty or obsolete persisted id has no current topic`() {
        assertNull(QuoteTopicsRepository(MemoryStore()).selectedTopic())
        assertNull(QuoteTopicsRepository(MemoryStore("removed_topic")).selectedTopic())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `repository also refuses invalid ids`() {
        QuoteTopicsRepository(MemoryStore()).selectTopic("unknown")
    }

    private class MemoryStore(private var id: String? = null) : TopicSelectionStore {
        var writes = 0
            private set
        override fun selectedId(): String? = id
        override fun saveSelectedId(id: String) {
            this.id = id
            writes++
        }
    }
}
