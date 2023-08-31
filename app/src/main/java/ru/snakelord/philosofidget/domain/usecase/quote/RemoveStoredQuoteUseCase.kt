package ru.snakelord.philosofidget.domain.usecase.quote

import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase

class RemoveStoredQuoteUseCase(private val quoteRepository: QuoteRepository) : CoroutineUseCase<Unit> {
    override suspend fun invoke() = quoteRepository.removeStoredQuote()
}