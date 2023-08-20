package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings

abstract class WidgetSettingsBaseViewHolder<Item : WidgetSettings>(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {
    protected val context: Context = itemView.context
    abstract fun bind(item: Item)
}