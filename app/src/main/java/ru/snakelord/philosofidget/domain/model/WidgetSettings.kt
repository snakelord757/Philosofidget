package ru.snakelord.philosofidget.domain.model

import androidx.annotation.StringRes

sealed class WidgetSettings {
    data class Toggle(
        @StringRes val titleRes: Int,
        val currentValue: Boolean,
        val toggleTarget: ToggleTarget
    ) : WidgetSettings() {
        enum class ToggleTarget {
            AUTHOR_VISIBILITY
        }
    }
}
