package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import ru.snakelord.philosofidget.databinding.WidgetSettingsSeekbarBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings

class SeekBarViewHolder(
    private val binding: WidgetSettingsSeekbarBinding,
    private val onSeekBarValueChangedCallback: (Int, WidgetSettings.SeekBar.SeekBarTarget) -> Unit
) : WidgetSettingsBaseViewHolder<WidgetSettings.SeekBar>(binding) {
    override fun bind(item: WidgetSettings.SeekBar) {
        binding.seekbarTitle.text = item.title
        binding.minValue.text = item.minValue.toString()
        binding.maxValue.text = item.maxValue.toString()
        binding.seekbar.progress = (item.currentValue * MAX_PERCENT) / item.maxValue
        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currentValue = maxOf(
                    ((progress * item.maxValue) / MAX_PERCENT),
                    item.minValue
                )
                onSeekBarValueChangedCallback.invoke(currentValue, item.seekBarTarget)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private companion object {
        const val MAX_PERCENT = 100
    }
}