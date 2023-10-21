package ru.snakelord.philosofidget.presentation.mapper

import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.presentation.model.WidgetState

class QuoteWidgetStateMapper {
    fun map(quote: Quote, widgetParams: QuoteWidgetParams) =
        WidgetState(
            quote = quote,
            isAuthorVisible = widgetParams.isAuthorVisible,
            quoteTextSize = widgetParams.quoteTextSize,
            quoteAuthorTextSize = widgetParams.quoteAuthorTextSize,
            quoteTextGravity = widgetParams.quoteTextGravity,
            quoteAuthorTextGravity = widgetParams.quoteAuthorTextGravity,
            quoteTextColor = widgetParams.quoteTextColor,
            quoteAuthorTextColor = widgetParams.quoteAuthorTextColor
        )
}