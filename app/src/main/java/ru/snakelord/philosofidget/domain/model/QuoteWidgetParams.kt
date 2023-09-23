package ru.snakelord.philosofidget.domain.model

data class QuoteWidgetParams(
    val isAuthorVisible: Boolean = true,
    val quoteLang: Lang = Lang.RU,
    val quoteTextSize: Float = 0F,
    val quoteAuthorTextSize: Float = 0F,
    val quoteUpdateTime: Long = 0L,
    val quoteTextGravity: TextGravity = TextGravity.START,
    val quoteAuthorTextGravity: TextGravity = TextGravity.END
)