package ru.snakelord.philosofidget.data.datasource.quote

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.snakelord.philosofidget.data.datasource.quote.network.QuoteService
import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Quote

class QuoteDataSourceImpl(
    private val quoteService: QuoteService,
    private val quoteSharedPreferences: SharedPreferences
) : QuoteDataSource {
    override suspend fun getQuote(key: Int, lang: String): QuoteDTO =
        quoteService.getQuote(
            method = CONST_METHOD,
            format = CONST_FORMAT,
            key = key,
            lang = lang
        )

    override suspend fun storeQuote(quote: Quote) = quoteSharedPreferences.edit { putString(PREFERENCES_QUOTE_KEY, Json.encodeToString(quote)) }

    override fun getStoredQuote(): Quote? = quoteSharedPreferences.getString(PREFERENCES_QUOTE_KEY, null)?.let { Json.decodeFromString(it) }

    override fun removeStoredQuote() = quoteSharedPreferences.edit { remove(PREFERENCES_QUOTE_KEY) }

    private companion object {
        const val PREFERENCES_QUOTE_KEY = "PREFERENCES_QUOTE_KEY"
        const val CONST_METHOD = "getQuote"
        const val CONST_FORMAT = "json"
    }
}