package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.databinding.FragmentWidgetSettingsBinding
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.adapter.WidgetSettingsAdapter
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.itemdecoration.WidgetSettingsItemDecoration

class WidgetSettingsFragment : Fragment(R.layout.fragment_widget_settings) {

    private var viewBinding: FragmentWidgetSettingsBinding? = null
    private val binding
        get() = viewBinding ?: error("ViewBinding isn't initialized!")

    private val widgetSettingsViewModel by viewModel<WidgetSettingsViewModel>()
    private val widgetSettingsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WidgetSettingsAdapter(
            toggleCallback = widgetSettingsViewModel::onToggleUpdated
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        widgetSettingsViewModel.loadWidgetSettings()
        widgetSettingsViewModel.loadQuoteWidgetParams()
        viewBinding = FragmentWidgetSettingsBinding.bind(view)
        with(binding) {
            settingsRecyclerView.adapter = widgetSettingsAdapter
            settingsRecyclerView.addItemDecoration(WidgetSettingsItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_20)))
            saveConfigurationButton.setOnClickListener { widgetSettingsViewModel.requestWidgetUpdate() }
        }
        with(widgetSettingsViewModel) {
            loadWidgetSettings()
            loadQuoteWidgetParams()
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    widgetSettings.collect(::setupWidgetSettings)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    quoteWidgetParams.collect(::updateWidgetPreview)
                }
            }
        }
    }

    private fun updateWidgetPreview(widgetParams: QuoteWidgetParams) {
        binding.quoteWidgetPreview.author.isVisible = widgetParams.isAuthorVisible
    }

    private fun setupWidgetSettings(widgetSettings: Array<WidgetSettings>) {
        widgetSettingsAdapter.setSettings(widgetSettings)
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "WidgetSettingsFragment"
        fun newInstance() = WidgetSettingsFragment()
    }
}