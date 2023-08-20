package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import ru.snakelord.philosofidget.databinding.WidgetSettingsToggleBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings

class ToggleViewHolder(
    private val binding: WidgetSettingsToggleBinding,
    private val toggleCallback: (Boolean, WidgetSettings.Toggle.ToggleTarget) -> Unit
) : WidgetSettingsBaseViewHolder<WidgetSettings.Toggle>(binding) {
    override fun bind(item: WidgetSettings.Toggle) {
        binding.toggleTitle.text = item.title
        binding.toggleSwitch.isChecked = item.currentValue
        binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked -> toggleCallback.invoke(isChecked, item.toggleTarget) }
    }
}