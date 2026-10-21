package ru.snakelord.philosofidget.quote

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSource
import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.data.repository.QuoteRepositoryImpl
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote
import java.io.IOException

class QuoteRepositoryTest {
    @Test fun `both languages are translated to API codes without changing keys`() = runBlocking {
        val source = RecordingQuoteDataSource()
        val repository = QuoteRepositoryImpl(source)
        assertSame(source.dto, repository.getQuote(0, Lang.RU))
        assertSame(source.dto, repository.getQuote(999998, Lang.ENG))
        assertEquals(listOf(0 to "ru", 999998 to "en"), source.requests)
    }

    @Test fun `store read replace and remove preserve quote values`() = runBlocking {
        val source = RecordingQuoteDataSource()
        val repository = QuoteRepositoryImpl(source)
        assertNull(repository.getStoredQuote())
        val first = Quote("First", "Author")
        repository.storeQuote(first)
        assertSame(first, source.stored)
        assertSame(first, repository.getStoredQuote())
        val replacement = Quote("Second", "")
        repository.storeQuote(replacement)
        assertSame(replacement, repository.getStoredQuote())
        repository.removeStoredQuote()
        assertNull(repository.getStoredQuote())
        assertEquals(1, source.removals)
    }

    @Test fun `network and storage failures reach caller unchanged`() = runBlocking {
        val source = RecordingQuoteDataSource()
        val repository = QuoteRepositoryImpl(source)
        val failure = IOException("source unavailable")
        source.failure = failure
        assertSame(failure, runCatching { repository.getQuote(12, Lang.RU) }.exceptionOrNull())
        assertSame(failure, runCatching { repository.storeQuote(Quote("Text", "")) }.exceptionOrNull())
        assertSame(failure, runCatching { repository.getStoredQuote() }.exceptionOrNull())
        assertSame(failure, runCatching { repository.removeStoredQuote() }.exceptionOrNull())
    }
}

private class RecordingQuoteDataSource : QuoteDataSource {
    val dto = QuoteDTO("Text", "Author", "Sender", "sender-link", "quote-link")
    val requests = mutableListOf<Pair<Int, String>>()
    var stored: Quote? = null
    var removals = 0
    var failure: Exception? = null
    override suspend fun getQuote(key: Int, lang: String): QuoteDTO {
        failure?.let { throw it }
        requests += key to lang
        return dto
    }
    override suspend fun storeQuote(quote: Quote) {
        failure?.let { throw it }
        stored = quote
    }
    override fun getStoredQuote(): Quote? {
        failure?.let { throw it }
        return stored
    }
    override fun removeStoredQuote() {
        failure?.let { throw it }
        removals++
        stored = null
    }
}
