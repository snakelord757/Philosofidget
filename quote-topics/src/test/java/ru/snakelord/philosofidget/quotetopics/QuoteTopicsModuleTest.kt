package ru.snakelord.philosofidget.quotetopics

import org.junit.Assert.*
import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import ru.snakelord.philosofidget.quotetopics.data.TopicSelectionDataSource
import ru.snakelord.philosofidget.quotetopics.di.quoteTopicsModule
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsInteractor
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsRepository
import ru.snakelord.philosofidget.quotetopics.presentation.QuoteTopicsViewModel

class QuoteTopicsModuleTest {
    @Test fun `module supplies separate view models observing the shared repository`() {
        val storage = object : TopicSelectionDataSource {
            var selectedId: String? = null
            override fun getSelectedTopicId() = selectedId
            override fun setSelectedTopicId(id: String) { selectedId = id }
        }
        val application = koinApplication {
            modules(quoteTopicsModule, module {
                single<TopicSelectionDataSource> { storage }
            })
        }
        try {
            val koin = application.koin
            val first = koin.get<QuoteTopicsViewModel>()
            val second = koin.get<QuoteTopicsViewModel>()
            assertNotSame(first, second)
            assertNotSame(koin.get<QuoteTopicsInteractor>(), koin.get<QuoteTopicsInteractor>())
            assertSame(koin.get<QuoteTopicsRepository>(), koin.get<QuoteTopicsRepository>())
            assertEquals(18, first.topics.size)
            assertTrue(first.selectTopic("wisdom"))
            assertEquals("wisdom", second.selectedTopic.value?.id)
            assertEquals("wisdom", storage.selectedId)
        } finally {
            application.close()
        }
    }
}
