package ru.snakelord.philosofidget.presentation.mapper

import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.presentation.model.QuoteWidgetState

class QuoteWidgetStateMapper {
    fun map(quote: Quote, widgetParams: QuoteWidgetParams) =
        QuoteWidgetState.WidgetState(
            quote = quote,
            isAuthorVisible = widgetParams.isAuthorVisible
        )
}