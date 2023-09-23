package ru.snakelord.philosofidget.domain.model

import android.view.Gravity.START
import android.view.Gravity.END
import android.view.Gravity.CENTER

enum class TextGravity {
    START,
    END,
    CENTER
}

fun TextGravity.resolveGravity(): Int = when(this) {
    TextGravity.START -> START
    TextGravity.END -> END
    TextGravity.CENTER -> CENTER
}