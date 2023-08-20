package ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.viewholder

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import ru.snakelord.philosofidget.databinding.WidgetSettingsSpinnerBinding
import ru.snakelord.philosofidget.domain.model.WidgetSettings

class SpinnerViewHolder(
    private val binding: WidgetSettingsSpinnerBinding,
    private val languageSpinnerCallback: (String) -> Unit
) : WidgetSettingsBaseViewHolder<WidgetSettings.Spinner>(binding) {
    override fun bind(item: WidgetSettings.Spinner) {
        binding.spinnerTitle.text = item.title
        val variantsAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, item.variants)
        variantsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.adapter = variantsAdapter
        binding.spinner.setSelection(variantsAdapter.getPosition(item.currentVariant))
        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                variantsAdapter.getItem(position)?.let(languageSpinnerCallback)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }
}