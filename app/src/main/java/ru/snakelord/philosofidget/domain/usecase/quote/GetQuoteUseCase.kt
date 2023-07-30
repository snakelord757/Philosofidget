package ru.snakelord.philosofidget.domain.usecase.quote

import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.ext.Mapper
import ru.snakelord.philosofidget.domain.model.GetQuoteParams
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase

class GetQuoteUseCase(
    private val quoteRepository: QuoteRepository,
    private val mapper: Mapper<QuoteDTO, Quote>
) : CoroutineUseCase<GetQuoteParams, Quote> {
    override suspend fun invoke(params: GetQuoteParams): Quote = with(params) {
        mapper.invoke(quoteRepository.getQuote(key, lang))
    }
}