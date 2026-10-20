package ru.snakelord.philosofidget.presentation.view.quote_topics

import android.content.Context
import ru.snakelord.philosofidget.R

/** Local selection for the mock topic catalog; quote loading is unchanged. */
object QuoteTopicSelection {
    private const val PREFERENCES = "quote_topics"
    private const val SELECTED_TOPIC = "selected_topic"

    fun getIndex(context: Context): Int = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        .getInt(SELECTED_TOPIC, 0)
        .takeIf { it in context.resources.getStringArray(R.array.quote_topics).indices } ?: 0

    fun getTitle(context: Context): String = context.resources.getStringArray(R.array.quote_topics)[getIndex(context)]

    fun select(context: Context, index: Int) {
        require(index in context.resources.getStringArray(R.array.quote_topics).indices)
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putInt(SELECTED_TOPIC, index).apply()
    }
}
