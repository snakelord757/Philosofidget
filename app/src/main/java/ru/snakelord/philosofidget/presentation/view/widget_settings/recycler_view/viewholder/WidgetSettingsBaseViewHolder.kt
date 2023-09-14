package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.snakelord.philosofidget.domain.model.WidgetSettings

abstract class WidgetSettingsBaseViewHolder<Item : WidgetSettings>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: Item)
}