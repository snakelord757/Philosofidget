package ru.snakelord.philosofidget.presentation.model

import ru.snakelord.philosofidget.domain.model.Quote

sealed class QuoteWidgetState {
    data object Loading : QuoteWidgetState()

    data class WidgetState(val quote: Quote) : QuoteWidgetState()

    object Error : QuoteWidgetState()
}