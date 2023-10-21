package ru.snakelord.philosofidget.presentation.widget.view_delegate

import android.widget.RemoteViews
import androidx.annotation.ColorInt
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.TextGravity
import ru.snakelord.philosofidget.presentation.model.WidgetState

interface WidgetViewDelegate {
    val widgetView: RemoteViews

    fun setQuote(quote: Quote)

    fun setQuoteTextSize(size: Float)

    fun setQuoteAuthorTextSize(size: Float)

    fun setQuoteTextColor(@ColorInt color: Int)

    fun setQuoteAuthorTextColor(@ColorInt color: Int)

    fun isAuthorVisible(isAuthorVisible: Boolean)

    fun setQuoteTextGravity(textGravity: TextGravity)

    fun setQuoteAuthorTextGravity(textGravity: TextGravity)

    fun setupWidget(widgetState: WidgetState)
}