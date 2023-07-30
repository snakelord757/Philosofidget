package ru.snakelord.philosofidget.domain.mapper

import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.ext.Mapper
import ru.snakelord.philosofidget.domain.model.Quote

val quoteDtoMapper: Mapper<QuoteDTO, Quote> = {
    Quote(quoteText = it.quoteText, quoteAuthor = it.quoteAuthor)
}