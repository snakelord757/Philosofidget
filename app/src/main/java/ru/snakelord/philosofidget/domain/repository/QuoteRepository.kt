package ru.snakelord.philosofidget.domain.repository

import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote

interface QuoteRepository {
    suspend fun getQuote(key: Int, lang: Lang): QuoteDTO

    suspend fun storeQuote(quote: Quote)

    suspend fun getStoredQuote(): Quote?

    suspend fun removeStoredQuote()
}