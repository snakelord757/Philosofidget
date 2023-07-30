package ru.snakelord.philosofidget.presentation.mapper

import ru.snakelord.philosofidget.domain.ext.Mapper
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.presentation.model.QuoteWidgetState

val quoteMapper: Mapper<Quote, QuoteWidgetState> = { QuoteWidgetState.WidgetState(it) }