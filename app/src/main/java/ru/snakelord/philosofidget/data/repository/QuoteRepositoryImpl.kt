package ru.snakelord.philosofidget.data.repository

import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSource
import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.repository.QuoteRepository

class QuoteRepositoryImpl(
    private val quoteDataSource: QuoteDataSource
) : QuoteRepository {
    override suspend fun getQuote(key: Int, lang: Lang): QuoteDTO = quoteDataSource.getQuote(key, lang.code)
    override suspend fun storeQuote(quote: Quote) = quoteDataSource.storeQuote(quote)
    override suspend fun getStoredQuote(): Quote? = quoteDataSource.getStoredQuote()
    override suspend fun removeStoredQuote() = quoteDataSource.removeStoredQuote()
}