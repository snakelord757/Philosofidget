package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import ru.snakelord.philosofidget.databinding.WidgetSettingsColorPickerBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings

class ColorPickerViewHolder(
    private val binding: WidgetSettingsColorPickerBinding,
    private val openColorPickerCallback: (WidgetSettings.ColorPicker.ColorPickerTarget) -> Unit
) : WidgetSettingsBaseViewHolder<WidgetSettings.ColorPicker>(binding.root) {

    init {
        binding.colorViewContainer.clipToOutline = true
    }

    override fun bind(item: WidgetSettings.ColorPicker) {
        binding.colorViewContainer.setOnClickListener { openColorPickerCallback.invoke(item.colorPickerTarget) }
        binding.colorPickerTitle.text = item.title
        binding.colorView.setBackgroundColor(item.currentColor)
    }
}