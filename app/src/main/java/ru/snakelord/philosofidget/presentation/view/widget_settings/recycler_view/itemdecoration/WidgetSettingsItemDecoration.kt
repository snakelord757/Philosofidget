package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class WidgetSettingsItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemPosition = parent.getChildLayoutPosition(view)
        val isFirst = itemPosition == EMPTY_SPACING
        outRect.apply {
            top = if (isFirst) spacing else EMPTY_SPACING
            bottom = spacing
        }
    }

    private companion object {
        const val EMPTY_SPACING = 0
    }
}