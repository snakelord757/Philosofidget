package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import ru.snakelord.philosofidget.databinding.WidgetSettingsSliderBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import kotlin.math.roundToInt

class SliderViewHolder(
    private val binding: WidgetSettingsSliderBinding,
    private val onSliderValueChangedCallback: (Float, WidgetSettings.Slider.SliderTarget) -> Unit
) : WidgetSettingsBaseViewHolder<WidgetSettings.Slider>(binding) {
    override fun bind(item: WidgetSettings.Slider) = with(binding) {
        sliderTitle.text = item.title
        minValue.text = item.minValue.roundToInt().toString()
        maxValue.text = item.maxValue.roundToInt().toString()
        slider.valueTo = item.maxValue
        slider.valueFrom = item.minValue
        slider.value = item.currentValue
        if (item.formatToInt) slider.setLabelFormatter { value -> value.roundToInt().toString() }
        slider.addOnChangeListener { _, value, _ -> onSliderValueChangedCallback.invoke(value, item.sliderTarget) }
    }
}