package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import ru.snakelord.philosofidget.databinding.WidgetSettingsSpinnerBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Spinner
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Spinner.SpinnerTarget

class SpinnerViewHolder(
    private val binding: WidgetSettingsSpinnerBinding,
    private val spinnerCallback: (String, SpinnerTarget) -> Unit
) : WidgetSettingsBaseViewHolder<Spinner>(binding.root) {
    override fun bind(item: Spinner) {
        binding.spinnerTitle.text = item.title
        val variantsAdapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, item.variants)
        variantsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.adapter = variantsAdapter
        binding.spinner.setSelection(variantsAdapter.getPosition(item.currentVariant), false)
        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                variantsAdapter.getItem(position)?.let { spinnerCallback.invoke(it, item.spinnerTarget) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }
}