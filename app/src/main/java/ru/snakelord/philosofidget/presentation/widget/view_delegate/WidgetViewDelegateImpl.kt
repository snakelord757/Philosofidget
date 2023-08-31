package ru.snakelord.philosofidget.presentation.widget.view_delegate

import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote

class WidgetViewDelegateImpl(override val widgetView: RemoteViews) : WidgetViewDelegate {

    override var selectedLanguage: Lang = Lang.RU

    override fun setProgressVisibility(isProgressVisible: Boolean) {
        widgetView.setViewVisibility(R.id.progressBar, resolveVisibility(isProgressVisible))
        widgetView.setViewVisibility(R.id.quoteContainer, resolveVisibility(isProgressVisible.not()))
    }

    override fun setQuote(quote: Quote) {
        widgetView.setTextViewText(R.id.quote, quote.quoteText)
        val quoteAuthorText = quote.quoteAuthor
        widgetView.setViewVisibility(R.id.author, resolveVisibility(quoteAuthorText.isNotEmpty()))
        widgetView.setTextViewText(R.id.author, quoteAuthorText)
    }

    override fun setQuoteTextSize(size: Float) = setTextSize(R.id.quote, size)

    override fun setQuoteAuthorTextSize(size: Float) = setTextSize(R.id.author, size)

    override fun setQuoteTextColor(@ColorInt color: Int) {
        widgetView.setTextColor(R.id.quote, color)
    }

    override fun setQuoteAuthorTextColor(@ColorInt color: Int) {
        widgetView.setTextColor(R.id.author, color)
    }

    override fun isAuthorVisible(isAuthorVisible: Boolean) = widgetView.setViewVisibility(R.id.author, resolveVisibility(isAuthorVisible))

    private fun resolveVisibility(isViewVisible: Boolean): Int = if (isViewVisible) View.VISIBLE else View.GONE

    private fun setTextSize(viewId: Int, size: Float) = widgetView.setTextViewTextSize(viewId, COMPLEX_UNIT_SP, size)
}