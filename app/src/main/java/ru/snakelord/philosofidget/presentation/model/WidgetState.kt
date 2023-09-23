package ru.snakelord.philosofidget.presentation.model

import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.model.TextGravity

data class WidgetState(
    val quote: Quote,
    val isAuthorVisible: Boolean,
    val quoteTextSize: Float,
    val quoteAuthorTextSize: Float,
    val quoteTextGravity: TextGravity,
    val quoteAuthorTextGravity: TextGravity
)