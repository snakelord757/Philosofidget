package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.snakelord.philosofidget.databinding.WidgetSettingsColorPickerBinding
import ru.snakelord.philosofidget.databinding.WidgetSettingsSliderBinding
import ru.snakelord.philosofidget.databinding.WidgetSettingsSpinnerBinding
import ru.snakelord.philosofidget.databinding.WidgetSettingsToggleBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder.ColorPickerViewHolder
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder.SliderViewHolder
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder.SpinnerViewHolder
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder.ToggleViewHolder
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder.WidgetSettingsBaseViewHolder

class WidgetSettingsAdapter(
    private val toggleCallback: (Boolean, WidgetSettings.Toggle.ToggleTarget) -> Unit,
    private val spinnerCallback: (String, WidgetSettings.Spinner.SpinnerTarget) -> Unit,
    private val sliderCallback: (Float, WidgetSettings.Slider.SliderTarget) -> Unit,
    private val openColorPickerCallback: (WidgetSettings.ColorPicker.ColorPickerTarget) -> Unit
) : RecyclerView.Adapter<WidgetSettingsBaseViewHolder<WidgetSettings>>() {

    private val widgetSettings = mutableListOf<WidgetSettings>()

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetSettingsBaseViewHolder<WidgetSettings> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (WidgetSettingsViewType.values()[viewType]) {
            WidgetSettingsViewType.TOGGLE -> ToggleViewHolder(
                binding = WidgetSettingsToggleBinding.inflate(layoutInflater, parent, false),
                toggleCallback = toggleCallback
            )

            WidgetSettingsViewType.SPINNER -> SpinnerViewHolder(
                binding = WidgetSettingsSpinnerBinding.inflate(layoutInflater, parent, false),
                spinnerCallback = spinnerCallback
            )

            WidgetSettingsViewType.SLIDER -> SliderViewHolder(
                binding = WidgetSettingsSliderBinding.inflate(layoutInflater, parent, false),
                onSliderValueChangedCallback = sliderCallback
            )

            WidgetSettingsViewType.COLOR_PICKER -> ColorPickerViewHolder(
                binding = WidgetSettingsColorPickerBinding.inflate(layoutInflater, parent, false),
                openColorPickerCallback = openColorPickerCallback
            )
        } as WidgetSettingsBaseViewHolder<WidgetSettings>
    }

    override fun onBindViewHolder(holder: WidgetSettingsBaseViewHolder<WidgetSettings>, position: Int) {
        holder.bind(widgetSettings[position])
    }

    override fun getItemViewType(position: Int): Int = when (widgetSettings[position]) {
        is WidgetSettings.Toggle -> WidgetSettingsViewType.TOGGLE
        is WidgetSettings.Spinner -> WidgetSettingsViewType.SPINNER
        is WidgetSettings.Slider -> WidgetSettingsViewType.SLIDER
        is WidgetSettings.ColorPicker -> WidgetSettingsViewType.COLOR_PICKER
    }.ordinal

    override fun getItemCount(): Int = widgetSettings.size

    fun setSettings(widgetSettings: Array<WidgetSettings>) {
        this.widgetSettings.clear()
        this.widgetSettings.addAll(widgetSettings)
        notifyDataSetChanged()
    }

    private enum class WidgetSettingsViewType {
        TOGGLE,
        SPINNER,
        SLIDER,
        COLOR_PICKER
    }
}