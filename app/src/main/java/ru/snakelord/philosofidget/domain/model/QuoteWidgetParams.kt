package ru.snakelord.philosofidget.domain.model

data class QuoteWidgetParams(
    val isAuthorVisible: Boolean = true,
    val quoteLang: Lang = Lang.RU
)