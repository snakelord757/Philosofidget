package ru.snakelord.philosofidget.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val quoteText: String,
    val quoteAuthor: String
)