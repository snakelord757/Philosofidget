package ru.snakelord.philosofidget.data.model

import kotlinx.serialization.Serializable

@Serializable
data class QuoteDTO(
    val quoteText: String,
    val quoteAuthor: String,
    val senderName: String,
    val senderLink: String,
    val quoteLink: String
)