package ru.snakelord.philosofidget.quote

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.mapper.QuoteDTOMapper
import ru.snakelord.philosofidget.domain.model.GetQuoteParams
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.quote.GetQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetStoredQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.RemoveStoredQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.StoreQuoteUseCase
import java.io.IOException

class QuoteUseCasesTest {
    @Test fun `fetch forwards parameters and returns mapped quote`() = runBlocking {
        val repository = RecordingQuoteRepository()
        val useCase = GetQuoteUseCase(repository, QuoteDTOMapper())
        for (lang in Lang.entries) {
            assertEquals(Quote("Quote", "Author"), useCase.invoke(GetQuoteParams(123, lang)))
        }
        assertEquals(listOf(123 to Lang.RU, 123 to Lang.ENG), repository.requests)
        assertNull(repository.stored)
    }

    @Test fun `storage use cases handle absent stored replaced and removed quote`() = runBlocking {
        val repository = RecordingQuoteRepository()
        val get = GetStoredQuoteUseCase(repository)
        val store = StoreQuoteUseCase(repository)
        val remove = RemoveStoredQuoteUseCase(repository)
        assertNull(get.invoke())
        val quote = Quote("Привет\n\"World\"", "")
        store.invoke(quote)
        assertSame(quote, repository.stored)
        assertSame(quote, get.invoke())
        val second = Quote("Replacement", "Author")
        store.invoke(second)
        assertSame(second, get.invoke())
        remove.invoke()
        assertNull(get.invoke())
        remove.invoke()
        assertNull(get.invoke())
        assertEquals(2, repository.removals)
    }

    @Test fun `use cases propagate repository errors and cancellation`() = runBlocking {
        val repository = RecordingQuoteRepository()
        for (failure in listOf(IOException("offline"), CancellationException("cancelled"))) {
            repository.failure = failure
            assertSame(failure, runCatching { GetQuoteUseCase(repository, QuoteDTOMapper()).invoke(GetQuoteParams(1, Lang.RU)) }.exceptionOrNull())
            assertSame(failure, runCatching { StoreQuoteUseCase(repository).invoke(Quote("Text", "")) }.exceptionOrNull())
            assertSame(failure, runCatching { GetStoredQuoteUseCase(repository).invoke() }.exceptionOrNull())
            assertSame(failure, runCatching { RemoveStoredQuoteUseCase(repository).invoke() }.exceptionOrNull())
        }
    }
}

private class RecordingQuoteRepository : QuoteRepository {
    val requests = mutableListOf<Pair<Int, Lang>>()
    var stored: Quote? = null
    var removals = 0
    var failure: Exception? = null
    override suspend fun getQuote(key: Int, lang: Lang): QuoteDTO {
        failure?.let { throw it }
        requests += key to lang
        return QuoteDTO("Quote", "Author", "Sender", "sender-link", "quote-link")
    }
    override suspend fun storeQuote(quote: Quote) {
        failure?.let { throw it }
        stored = quote
    }
    override suspend fun getStoredQuote(): Quote? {
        failure?.let { throw it }
        return stored
    }
    override suspend fun removeStoredQuote() {
        failure?.let { throw it }
        removals++
        stored = null
    }
}
