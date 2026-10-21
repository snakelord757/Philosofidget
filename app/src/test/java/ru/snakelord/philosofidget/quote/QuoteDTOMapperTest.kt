package ru.snakelord.philosofidget.quote

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.mapper.QuoteDTOMapper
import ru.snakelord.philosofidget.domain.model.Quote

class QuoteDTOMapperTest {
    @Test fun `mapper preserves whitespace unicode and punctuation and omits sender metadata`() {
        val dto = QuoteDTO("  Мысль\n\"Don't\" 😀  ", " Автор ", "Sender", "sender-link", "quote-link")
        assertEquals(Quote(dto.quoteText, dto.quoteAuthor), QuoteDTOMapper().map(dto))
    }

    @Test fun `empty quote and anonymous author are accepted`() {
        assertEquals(Quote("", ""), QuoteDTOMapper().map(QuoteDTO("", "", "", "", "")))
    }

    @Test fun `API response deserializes into quote`() {
        val dto = Json.decodeFromString<QuoteDTO>("""{"quoteText":"Мысль","quoteAuthor":"Автор","senderName":"Sender","senderLink":"s","quoteLink":"q"}""")
        assertEquals(Quote("Мысль", "Автор"), QuoteDTOMapper().map(dto))
        assertEquals("Sender", dto.senderName)
        assertEquals("s", dto.senderLink)
        assertEquals("q", dto.quoteLink)
    }

    @Test fun `invalid and incomplete API responses fail deserialization`() {
        for (json in listOf("not json", "{}", """{"quoteText":null,"quoteAuthor":"","senderName":"","senderLink":"","quoteLink":""}""")) {
            assertTrue(runCatching { Json.decodeFromString<QuoteDTO>(json) }.exceptionOrNull() is SerializationException)
        }
    }
}
