package ru.snakelord.philosofidget.data.datasource.quote

import ru.snakelord.philosofidget.data.datasource.quote.network.QuoteService
import ru.snakelord.philosofidget.data.model.QuoteDTO

class QuoteDataSourceImpl(private val quoteService: QuoteService) : QuoteDataSource {
    override suspend fun getQuote(key: Int, lang: String): QuoteDTO =
        quoteService.getQuote(
            method = CONST_METHOD,
            format = CONST_FORMAT,
            key = key,
            lang = lang
        )

    private companion object {
        const val CONST_METHOD = "getQuote"
        const val CONST_FORMAT = "json"
    }
}