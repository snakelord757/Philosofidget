package ru.snakelord.philosofidget.data.datasource.quote

import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Quote

interface QuoteDataSource {
    suspend fun getQuote(key: Int, lang: String): QuoteDTO

    suspend fun storeQuote(quote: Quote)

    fun getStoredQuote(): Quote?

    fun removeStoredQuote()
}