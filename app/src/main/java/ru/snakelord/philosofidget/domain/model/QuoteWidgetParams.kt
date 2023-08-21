package ru.snakelord.philosofidget.domain.model

data class QuoteWidgetParams(
    val isAuthorVisible: Boolean = true,
    val quoteLang: Lang = Lang.RU,
    val quoteTextSize: Float = 0f,
    val quoteAuthorTextSize: Float = 0f
)