package ru.snakelord.philosofidget.domain.model

sealed class WidgetSettings {
    data class Toggle(
        val title: String,
        val currentValue: Boolean,
        val toggleTarget: ToggleTarget
    ) : WidgetSettings() {
        enum class ToggleTarget {
            AUTHOR_VISIBILITY
        }
    }

    data class Spinner(
        val title: String,
        val currentVariant: String,
        val variants: List<String>,
        val spinnerTarget: SpinnerTarget
    ) : WidgetSettings() {
        enum class SpinnerTarget {
            LANGUAGE,
            QUOTE_TEXT_GRAVITY,
            QUOTE_AUTHOR_GRAVITY
        }
    }

    data class Slider(
        val title: String,
        val currentValue: Float,
        val minValue: Float,
        val maxValue: Float,
        val sliderTarget: SliderTarget
    ) : WidgetSettings() {
        enum class SliderTarget {
            QUOTE_TEXT_SIZE,
            QUOTE_AUTHOR_TEXT_SIZE,
            QUOTE_UPDATE_TIME
        }
    }
}
