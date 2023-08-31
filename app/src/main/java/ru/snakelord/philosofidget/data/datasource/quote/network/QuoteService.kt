package ru.snakelord.philosofidget.data.datasource.quote.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.snakelord.philosofidget.data.model.QuoteDTO

interface QuoteService {
    @GET(REQUEST_PATH)
    suspend fun getQuote(
        @Query(QUERY_PARAM_METHOD) method: String,
        @Query(QUERY_PARAM_FORMAT) format: String,
        @Query(QUERY_PARAM_KEY) key: Int,
        @Query(QUERY_PARAM_LANG) lang: String
    ): QuoteDTO

    private companion object {
        const val REQUEST_PATH = "1.0/"
        const val QUERY_PARAM_METHOD = "method"
        const val QUERY_PARAM_FORMAT = "format"
        const val QUERY_PARAM_KEY = "key"
        const val QUERY_PARAM_LANG = "lang"
    }
}