package ru.snakelord.philosofidget.domain.repository

import ru.snakelord.philosofidget.data.model.QuoteDTO
import ru.snakelord.philosofidget.domain.model.Lang
interface QuoteRepository {
    suspend fun getQuote(key: Int, lang: Lang): QuoteDTO
}