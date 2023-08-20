package ru.snakelord.philosofidget.domain.usecase.quote

import ru.snakelord.philosofidget.domain.mapper.QuoteDTOMapper
import ru.snakelord.philosofidget.domain.model.GetQuoteParams
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCaseWithParams

class GetQuoteUseCase(
    private val quoteRepository: QuoteRepository,
    private val mapper: QuoteDTOMapper,
) : CoroutineUseCaseWithParams<GetQuoteParams, Quote> {
    override suspend fun invoke(params: GetQuoteParams): Quote = with(params) {
        mapper.map(quoteRepository.getQuote(key, lang))
    }
}