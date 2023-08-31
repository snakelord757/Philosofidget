package ru.snakelord.philosofidget.presentation.widget.view_delegate

import android.widget.RemoteViews
import androidx.annotation.ColorInt
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote

interface WidgetViewDelegate {
    val widgetView: RemoteViews

    var selectedLanguage: Lang

    fun setProgressVisibility(isProgressVisible: Boolean)

    fun setQuote(quote: Quote)

    fun setQuoteTextSize(size: Float)

    fun setQuoteAuthorTextSize(size: Float)

    fun setQuoteTextColor(@ColorInt color: Int)

    fun setQuoteAuthorTextColor(@ColorInt color: Int)

    fun isAuthorVisible(isAuthorVisible: Boolean)
}