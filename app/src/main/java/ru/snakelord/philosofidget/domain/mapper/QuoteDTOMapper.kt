package ru.snakelord.philosofidget.domain.mapper

import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Quote

class QuoteDTOMapper {
    fun map(quoteDto: QuoteDTO): Quote = Quote(
        quoteText = quoteDto.quoteText,
        quoteAuthor = quoteDto.quoteAuthor
    )
}