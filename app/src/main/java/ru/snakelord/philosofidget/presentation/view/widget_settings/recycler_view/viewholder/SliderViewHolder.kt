package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import ru.snakelord.philosofidget.databinding.WidgetSettingsSliderBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import kotlin.math.roundToInt

class SliderViewHolder(
    private val binding: WidgetSettingsSliderBinding,
    private val onSliderValueChangedCallback: (Float, WidgetSettings.Slider.SliderTarget) -> Unit
) : WidgetSettingsBaseViewHolder<WidgetSettings.Slider>(binding.root) {
    override fun bind(item: WidgetSettings.Slider) {
        binding.sliderTitle.text = item.title
        binding.minValue.text = item.minValue.toInt().toString()
        binding.maxValue.text = item.maxValue.toInt().toString()
        setupSlider(item)
    }

    private fun setupSlider(item: WidgetSettings.Slider) = with(binding.slider) {
        valueTo = item.maxValue
        valueFrom = item.minValue
        value = item.currentValue
        setLabelFormatter { value -> value.roundToInt().toString() }
        addOnChangeListener { _, value, _ -> onSliderValueChangedCallback.invoke(value, item.sliderTarget) }
    }
}