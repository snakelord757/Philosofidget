package ru.snakelord.philosofidget.domain.model

import android.graphics.Color
import androidx.annotation.ColorInt

data class QuoteWidgetParams(
    val isAuthorVisible: Boolean = true,
    val quoteLang: Lang = Lang.RU,
    val quoteTextSize: Float = 0F,
    val quoteAuthorTextSize: Float = 0F,
    val quoteUpdateTime: Long = 0L,
    val quoteTextGravity: TextGravity = TextGravity.START,
    val quoteAuthorTextGravity: TextGravity = TextGravity.END,
    @ColorInt val quoteTextColor: Int = Color.WHITE,
    @ColorInt val quoteAuthorTextColor: Int = Color.WHITE
)