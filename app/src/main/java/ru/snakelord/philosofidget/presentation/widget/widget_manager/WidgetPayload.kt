package ru.snakelord.philosofidget.presentation.widget.widget_manager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WidgetPayload : Parcelable {
    QUOTE,
    QUOTE_TEXT_SIZE,
    AUTHOR_VISIBILITY,
    AUTHOR_TEXT_SIZE,
    QUOTE_LANGUAGE,
    QUOTE_UPDATE_TIME,
    QUOTE_TEXT_GRAVITY,
    QUOTE_AUTHOR_TEXT_GRAVITY
}