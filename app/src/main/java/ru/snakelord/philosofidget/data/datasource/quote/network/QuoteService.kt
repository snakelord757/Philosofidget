package ru.snakelord.philosofidget.data.datasource.quote.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.snakelord.philosofidget.data.model.QuoteDTO

interface QuoteService {
    @GET("1.0/")
    suspend fun getQuote(
        @Query("method") method: String,
        @Query("format") format: String,
        @Query("key") key: Int,
        @Query("lang") lang: String
    ): ru.snakelord.philosofidget.data.model.QuoteDTO
}