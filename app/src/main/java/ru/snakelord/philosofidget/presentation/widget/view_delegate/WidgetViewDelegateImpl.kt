package ru.snakelord.philosofidget.presentation.widget.view_delegate

import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.model.TextGravity

class WidgetViewDelegateImpl(override val widgetView: RemoteViews) : WidgetViewDelegate {

    private val quoteIdsMap = mapOf(
        TextGravity.START to R.id.quote_start,
        TextGravity.END to R.id.quote_end,
        TextGravity.CENTER to R.id.quote_center
    )

    private val quoteAuthorIdsMap = mapOf(
        TextGravity.START to R.id.author_start,
        TextGravity.END to R.id.author_end,
        TextGravity.CENTER to R.id.author_center,
    )

    override fun setQuote(quote: Quote) {
        quoteIdsMap.values.forEach { widgetView.setTextViewText(it, quote.quoteText) }
        quoteAuthorIdsMap.values.forEach { widgetView.setTextViewText(it, quote.quoteAuthor) }
        widgetView.setViewVisibility(R.id.progressBar, resolveVisibility(false))
    }

    override fun setQuoteTextSize(size: Float) = quoteIdsMap.values.forEach { setTextSize(it, size) }

    override fun setQuoteAuthorTextSize(size: Float) = quoteAuthorIdsMap.values.forEach { setTextSize(it, size) }

    override fun setQuoteTextColor(@ColorInt color: Int) = widgetView.setTextColor(R.id.quote, color)

    override fun setQuoteAuthorTextColor(@ColorInt color: Int) = widgetView.setTextColor(R.id.author, color)

    override fun isAuthorVisible(isAuthorVisible: Boolean) =
        quoteAuthorIdsMap.values.forEach { updateViewVisibility(viewId = it, isViewVisible = isAuthorVisible) }

    override fun setQuoteTextGravity(textGravity: TextGravity) {
        updateTextVisibility(viewsMap = quoteIdsMap, textGravity = textGravity)
    }

    override fun setQuoteAuthorTextGravity(textGravity: TextGravity) {
        updateTextVisibility(viewsMap = quoteAuthorIdsMap, textGravity = textGravity)
    }

    private fun updateTextVisibility(viewsMap: Map<TextGravity, Int>, textGravity: TextGravity) {
        val targetView = viewsMap.getValue(textGravity)
        updateViewVisibility(viewId = targetView, isViewVisible = true)
        viewsMap.values.filter { it != targetView }.forEach { updateViewVisibility(viewId = it, isViewVisible = false) }
    }

    private fun updateViewVisibility(viewId: Int, isViewVisible: Boolean) = widgetView.setViewVisibility(viewId, resolveVisibility(isViewVisible))

    private fun resolveVisibility(isViewVisible: Boolean): Int = if (isViewVisible) View.VISIBLE else View.GONE

    private fun setTextSize(viewId: Int, size: Float) = widgetView.setTextViewTextSize(viewId, COMPLEX_UNIT_SP, size)
}