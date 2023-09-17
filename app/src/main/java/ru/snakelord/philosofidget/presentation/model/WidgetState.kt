package ru.snakelord.philosofidget.presentation.model

import ru.snakelord.philosofidget.domain.model.Quote

data class WidgetState(
    val quote: Quote,
    val isAuthorVisible: Boolean,
    val quoteTextSize: Float,
    val quoteAuthorTextSize: Float
)