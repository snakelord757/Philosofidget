package ru.snakelord.philosofidget.presentation.view

import android.widget.RemoteViews
import ru.snakelord.randomquoteswidget.R

class WidgetViewDelegate(val remoteViews: RemoteViews) {
    fun setQuote(quote: String) {
        remoteViews.setTextViewText(R.id.quote, quote)
    }

    fun setQuoteAuthor(quoteAuthor: String) {
        remoteViews.setTextViewText(R.id.author, quoteAuthor)
    }
}