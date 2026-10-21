package ru.snakelord.philosofidget.quote

import android.content.SharedPreferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSourceImpl
import ru.snakelord.philosofidget.data.datasource.quote.network.QuoteService
import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.settings.SettingsPreferences
import java.io.IOException

class QuoteDataSourceTest {
    @Test fun `request uses getQuote JSON and supplied key and language`() = runBlocking {
        val service = RecordingQuoteService()
        val source = QuoteDataSourceImpl(service, SettingsPreferences())
        assertSame(service.result, source.getQuote(0, "ru"))
        assertSame(service.result, source.getQuote(999998, "en"))
        assertEquals(listOf(listOf("getQuote", "json", 0, "ru"), listOf("getQuote", "json", 999998, "en")), service.requests)
        assertNull(source.getStoredQuote())
    }

    @Test fun `network errors and cancellation are propagated`() = runBlocking {
        val service = RecordingQuoteService()
        val source = QuoteDataSourceImpl(service, SettingsPreferences())
        for (failure in listOf(IOException("offline"), CancellationException("cancelled"))) {
            service.failure = failure
            assertSame(failure, runCatching { source.getQuote(1, "ru") }.exceptionOrNull())
        }
    }

    @Test fun `missing quote returns null`() {
        assertNull(QuoteDataSourceImpl(RecordingQuoteService(), SettingsPreferences()).getStoredQuote())
    }

    @Test fun `storage read failure is propagated`() {
        val failure = IllegalStateException("cannot read preferences")
        val preferences = object : SharedPreferences by SettingsPreferences() {
            override fun getString(key: String?, defValue: String?): String? = throw failure
        }
        val source = QuoteDataSourceImpl(RecordingQuoteService(), preferences)
        assertSame(failure, runCatching { source.getStoredQuote() }.exceptionOrNull())
    }

    @Test fun `quote survives data source recreation with unicode escaping and anonymous author`() = runBlocking {
        val preferences = SettingsPreferences()
        val source = QuoteDataSourceImpl(RecordingQuoteService(), preferences)
        val quote = Quote("  Мысль\n\"Don't\" \\ 😀  ", "")
        source.storeQuote(quote)
        assertEquals(quote, QuoteDataSourceImpl(RecordingQuoteService(), preferences).getStoredQuote())
        val savedJson = preferences.getString("PREFERENCES_QUOTE_KEY", null)
        assertEquals(quote, Json.decodeFromString<Quote>(requireNotNull(savedJson)))
    }

    @Test fun `storing replaces prior quote and removal preserves unrelated preferences`() = runBlocking {
        val preferences = SettingsPreferences()
        preferences.edit().putString("unrelated", "keep").apply()
        val source = QuoteDataSourceImpl(RecordingQuoteService(), preferences)
        source.storeQuote(Quote("First", "Author"))
        val replacement = Quote("", "Other")
        source.storeQuote(replacement)
        assertEquals(replacement, source.getStoredQuote())
        source.removeStoredQuote()
        source.removeStoredQuote()
        assertNull(source.getStoredQuote())
        assertFalse(preferences.contains("PREFERENCES_QUOTE_KEY"))
        assertEquals("keep", preferences.getString("unrelated", null))
    }

    @Test fun `invalid stored JSON is surfaced without deleting original data`() {
        val preferences = SettingsPreferences()
        val source = QuoteDataSourceImpl(RecordingQuoteService(), preferences)
        for (invalid in listOf("", "broken", "{}", """{"quoteText":"Text"}""")) {
            preferences.edit().putString("PREFERENCES_QUOTE_KEY", invalid).apply()
            assertTrue(runCatching { source.getStoredQuote() }.exceptionOrNull() is SerializationException)
            assertEquals(invalid, preferences.getString("PREFERENCES_QUOTE_KEY", null))
        }
    }

    @Test fun `storage failures are propagated and existing data is preserved`() = runBlocking {
        val preferences = SettingsPreferences()
        val source = QuoteDataSourceImpl(RecordingQuoteService(), preferences)
        val quote = Quote("Saved", "Author")
        source.storeQuote(quote)
        val failure = IllegalStateException("storage unavailable")
        preferences.failure = failure
        assertSame(failure, runCatching { source.storeQuote(Quote("New", "")) }.exceptionOrNull())
        assertSame(failure, runCatching { source.removeStoredQuote() }.exceptionOrNull())
        preferences.failure = null
        assertEquals(quote, source.getStoredQuote())
    }
}

private class RecordingQuoteService : QuoteService {
    val requests = mutableListOf<List<Any>>()
    val result = QuoteDTO("Text", "Author", "Sender", "sender-link", "quote-link")
    var failure: Exception? = null
    override suspend fun getQuote(method: String, format: String, key: Int, lang: String): QuoteDTO {
        failure?.let { throw it }
        requests += listOf(method, format, key, lang)
        return result
    }
}
