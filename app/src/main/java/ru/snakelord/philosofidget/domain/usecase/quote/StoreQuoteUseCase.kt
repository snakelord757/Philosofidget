package ru.snakelord.philosofidget.domain.usecase.quote

import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCaseWithParams

class StoreQuoteUseCase(private val quoteRepository: QuoteRepository) : CoroutineUseCaseWithParams<Quote, Unit> {
    override suspend fun invoke(params: Quote) = quoteRepository.storeQuote(params)
}