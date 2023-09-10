package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.databinding.FragmentWidgetSettingsBinding
import ru.snakelord.philosofidget.domain.ext.subscribeOnLifecycle
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.presentation.model.ActionButtonState
import ru.snakelord.philosofidget.presentation.model.WidgetConfigurationState
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.adapter.WidgetSettingsAdapter
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.itemdecoration.WidgetSettingsItemDecoration

class WidgetSettingsFragment : Fragment(R.layout.fragment_widget_settings) {

    private var viewBinding: FragmentWidgetSettingsBinding? = null
    private val binding
        get() = viewBinding ?: error("ViewBinding isn't initialized!")

    private val widgetSettingsViewModel by viewModel<WidgetSettingsViewModel> {
        parametersOf(
            requireActivity().intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, WidgetSettingsViewModel.UNDEFINED_WIDGET_ID)
                ?: WidgetSettingsViewModel.UNDEFINED_WIDGET_ID
        )
    }

    private val widgetSettingsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WidgetSettingsAdapter(
            toggleCallback = widgetSettingsViewModel::onToggleUpdated,
            languageSpinnerCallback = widgetSettingsViewModel::onLanguageSelected,
            sliderCallback = widgetSettingsViewModel::onSliderValueChanged
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding = FragmentWidgetSettingsBinding.bind(view)
        initUi()
        subscribeToViewModel()
    }

    private fun initUi() = with(binding) {
        settingsRecyclerView.adapter = widgetSettingsAdapter
        settingsRecyclerView.addItemDecoration(WidgetSettingsItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_16)))
    }

    private fun subscribeToViewModel() = with(widgetSettingsViewModel) {
        loadWidgetSettings()
        loadQuoteWidgetParams()
        widgetSettings.subscribeOnLifecycle(viewLifecycleOwner, ::setupWidgetSettings)
        quoteWidgetParams.subscribeOnLifecycle(viewLifecycleOwner, ::updateWidgetPreview)
        widgetConfigurationState.subscribeOnLifecycle(viewLifecycleOwner, ::onConfigurationSaved)
        actionButtonState.subscribeOnLifecycle(viewLifecycleOwner, ::setupActionButton)
    }

    override fun onResume() {
        super.onResume()
        widgetSettingsViewModel.setupButtonState()
    }

    private fun updateWidgetPreview(widgetParams: QuoteWidgetParams) = with(binding.quoteWidgetPreview) {
        author.isVisible = widgetParams.isAuthorVisible
        quote.textSize = widgetParams.quoteTextSize
        author.textSize = widgetParams.quoteAuthorTextSize
    }

    private fun setupWidgetSettings(widgetSettings: Array<WidgetSettings>) {
        widgetSettingsAdapter.setSettings(widgetSettings)
    }

    private fun onConfigurationSaved(widgetConfigurationState: WidgetConfigurationState) {
        val hostActivity = requireActivity()
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetConfigurationState.targetWidgetId)
        val resultCode = if (widgetConfigurationState.isConfigurationSaved) Activity.RESULT_OK else Activity.RESULT_CANCELED
        hostActivity.setResult(resultCode, result)
        if (widgetConfigurationState.isConfigurationSaved) hostActivity.finish()
    }

    private fun setupActionButton(state: ActionButtonState?) {
        if (state == null) return
        binding.actionButton.text = state.title
        binding.actionButton.setOnClickListener { state.onClickAction.invoke() }
        binding.actionButton.isEnabled = state.isEnabled
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