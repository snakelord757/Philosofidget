package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import ru.snakelord.philosofidget.databinding.WidgetSettingsSliderBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import kotlin.math.roundToInt

class SliderViewHolder(
    private val binding: WidgetSettingsSliderBinding,
    private val onSliderValueChangedCallback: (Float, WidgetSettings.SeekBar.SeekBarTarget) -> Unit
) : WidgetSettingsBaseViewHolder<WidgetSettings.SeekBar>(binding) {
    override fun bind(item: WidgetSettings.SeekBar) = with(binding) {
        sliderTitle.text = item.title
        minValue.text = item.minValue.roundToInt().toString()
        maxValue.text = item.maxValue.roundToInt().toString()
        slider.valueTo = item.maxValue
        slider.valueFrom = item.minValue
        slider.value = item.currentValue
        slider.addOnChangeListener { _, value, _ -> onSliderValueChangedCallback.invoke(value, item.seekBarTarget) }
    }
}