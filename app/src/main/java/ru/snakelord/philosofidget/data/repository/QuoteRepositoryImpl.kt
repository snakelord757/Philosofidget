package ru.snakelord.philosofidget.data.repository

import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.repository.QuoteRepository

class QuoteRepositoryImpl(
    private val quoteDataSource: ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSource
) : QuoteRepository {
    override suspend fun getQuote(key: Int, lang: Lang): QuoteDTO = quoteDataSource.getQuote(key, lang.code)
}