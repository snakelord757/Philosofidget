package ru.snakelord.philosofidget

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.TextGravity
import ru.snakelord.philosofidget.presentation.mapper.QuoteWidgetStateMapper
import ru.snakelord.philosofidget.presentation.model.WidgetState

class WidgetStateMapperTest {
    private val mapper = QuoteWidgetStateMapper()

    @Test
    fun mapsQuoteAndAllAppearanceSettings() {
        val quote = Quote("Мысль\nвторая строка", "Автор")
        val params = QuoteWidgetParams(
            isAuthorVisible = false, quoteLang = Lang.ENG,
            quoteTextSize = 29.5F, quoteAuthorTextSize = 12.5F,
            quoteUpdateTime = 24L, quoteTextGravity = TextGravity.CENTER,
            quoteAuthorTextGravity = TextGravity.START,
            quoteTextColor = 0xFF123456.toInt(), quoteAuthorTextColor = 0xFFABCDEF.toInt()
        )

        assertEquals(
            WidgetState(quote, false, 29.5F, 12.5F, TextGravity.CENTER,
                TextGravity.START, 0xFF123456.toInt(), 0xFFABCDEF.toInt()),
            mapper.map(quote, params)
        )
    }

    @Test
    fun preservesEmptyQuoteAndDefaultAppearance() {
        val quote = Quote("", "")
        assertEquals(
            WidgetState(quote, true, 0F, 0F, TextGravity.START, TextGravity.END, -1, -1),
            mapper.map(quote, QuoteWidgetParams())
        )
    }

    @Test
    fun languageAndScheduleDoNotChangeDisplayedState() {
        val quote = Quote("Quote", "Author")
        val params = QuoteWidgetParams()
        assertEquals(mapper.map(quote, params),
            mapper.map(quote, params.copy(quoteLang = Lang.ENG, quoteUpdateTime = 24L)))
    }

    @Test
    fun mapsEachAlignmentIndependently() {
        for (quoteGravity in TextGravity.entries) {
            for (authorGravity in TextGravity.entries) {
                val state = mapper.map(Quote("Text", "Author"), QuoteWidgetParams(
                    quoteTextGravity = quoteGravity, quoteAuthorTextGravity = authorGravity))
                assertEquals(quoteGravity, state.quoteTextGravity)
                assertEquals(authorGravity, state.quoteAuthorTextGravity)
            }
        }
    }
}
