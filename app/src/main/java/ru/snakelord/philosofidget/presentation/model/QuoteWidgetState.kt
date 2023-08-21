package ru.snakelord.philosofidget.presentation.model

import ru.snakelord.philosofidget.domain.model.Quote

sealed class QuoteWidgetState {
    data object Loading : QuoteWidgetState()

    data class WidgetState(
        val quote: Quote,
        val isAuthorVisible: Boolean,
        val quoteTextSize: Float,
        val quoteAuthorTextSize: Float
    ) : QuoteWidgetState()

    object Error : QuoteWidgetState()
}