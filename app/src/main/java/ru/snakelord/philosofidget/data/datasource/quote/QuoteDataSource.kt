package ru.snakelord.philosofidget.data.datasource.quote

import ru.snakelord.philosofidget.data.model.QuoteDTO

interface QuoteDataSource {
    suspend fun getQuote(key: Int, lang: String): QuoteDTO
}