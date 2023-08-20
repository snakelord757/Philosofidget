package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.snakelord.philosofidget.databinding.WidgetSettingsToggleBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder.ToggleViewHolder
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder.WidgetSettingsBaseViewHolder

class WidgetSettingsAdapter(
    private val toggleCallback: (Boolean, WidgetSettings.Toggle.ToggleTarget) -> Unit
) : RecyclerView.Adapter<WidgetSettingsBaseViewHolder<WidgetSettings>>() {

    private val widgetSettings = mutableListOf<WidgetSettings>()

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetSettingsBaseViewHolder<WidgetSettings> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder = when (viewType) {
            TOGGLE_VIEW_TYPE -> ToggleViewHolder(
                binding = WidgetSettingsToggleBinding.inflate(layoutInflater, parent, false),
                toggleCallback = toggleCallback
            )

            else -> error("Unsupported viewType: $viewType. Check log for details")
        }
        return viewHolder as? WidgetSettingsBaseViewHolder<WidgetSettings>
            ?: error("Unable to cast $viewHolder to WidgetSettingsBaseViewHolder<WidgetSettings>")
    }

    override fun onBindViewHolder(holder: WidgetSettingsBaseViewHolder<WidgetSettings>, position: Int) {
        holder.bind(widgetSettings[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (widgetSettings[position]) {
            is WidgetSettings.Toggle -> TOGGLE_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int = widgetSettings.size

    fun setSettings(widgetSettings: Array<WidgetSettings>) {
        this.widgetSettings.clear()
        this.widgetSettings.addAll(widgetSettings)
        notifyDataSetChanged()
    }

    private companion object {
        const val TOGGLE_VIEW_TYPE = 0
    }
}