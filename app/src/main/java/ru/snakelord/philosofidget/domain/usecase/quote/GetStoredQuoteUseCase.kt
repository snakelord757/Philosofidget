package ru.snakelord.philosofidget.domain.usecase.quote

import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase

class GetStoredQuoteUseCase(private val quoteRepository: QuoteRepository) : CoroutineUseCase<Quote?> {
    override suspend fun invoke(): Quote? = quoteRepository.getStoredQuote()
}