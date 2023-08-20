package ru.snakelord.philosofidget.presentation.widget

import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.model.Quote

class WidgetViewDelegate(val remoteViews: RemoteViews) {

    fun setProgressVisibility(isProgressVisible: Boolean) {
        remoteViews.setViewVisibility(R.id.progressBar, resolveVisibility(isProgressVisible))
        remoteViews.setViewVisibility(R.id.quoteContainer, resolveVisibility(isProgressVisible.not()))
    }

    fun setQuote(quote: Quote) {
        remoteViews.setTextViewText(R.id.quote, quote.quoteText)
        val quoteAuthorText = quote.quoteAuthor
        remoteViews.setViewVisibility(R.id.author, resolveVisibility(quoteAuthorText.isNotEmpty()))
        remoteViews.setTextViewText(R.id.author, quoteAuthorText)
    }

    fun setQuoteTextSize(size: Float) = setTextSize(R.id.quote, size)

    fun setQuoteAuthorTextSize(size: Float) = setTextSize(R.id.author, size)

    fun setQuoteTextColor(@ColorInt color: Int) {
        remoteViews.setTextColor(R.id.quote, color)
    }

    fun setQuoteAuthorTextColor(@ColorInt color: Int) {
        remoteViews.setTextColor(R.id.author, color)
    }

    fun isAuthorVisible(isAuthorVisible: Boolean) = remoteViews.setViewVisibility(R.id.author, resolveVisibility(isAuthorVisible))

    private fun resolveVisibility(isViewVisible: Boolean): Int = if (isViewVisible) View.VISIBLE else View.GONE

    private fun setTextSize(viewId: Int, size: Float) = remoteViews.setTextViewTextSize(viewId, COMPLEX_UNIT_SP, size)
}