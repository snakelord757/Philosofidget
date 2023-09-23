package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.databinding.FragmentWidgetSettingsBinding
import ru.snakelord.philosofidget.domain.ext.subscribeOnLifecycle
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.model.resolveGravity
import ru.snakelord.philosofidget.presentation.model.ActionButtonState
import ru.snakelord.philosofidget.presentation.model.WidgetConfigurationState
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.adapter.WidgetSettingsAdapter
import ru.snakelord.philosofidget.presentation.view.widget_settings.recycler_view.itemdecoration.WidgetSettingsItemDecoration

class WidgetSettingsFragment : Fragment(R.layout.fragment_widget_settings) {

    private var viewBinding: FragmentWidgetSettingsBinding? = null
    private val binding
        get() = viewBinding ?: error("ViewBinding isn't initialized!")

    private val widgetSettingsViewModel by viewModel<WidgetSettingsViewModel> {
        parametersOf(arguments?.getInt(BUNDLE_WIDGET_ID_KEY, UNDEFINED_WIDGET_ID) ?: UNDEFINED_WIDGET_ID)
    }

    private val widgetSettingsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WidgetSettingsAdapter(
            toggleCallback = widgetSettingsViewModel::onToggleValueChanged,
            spinnerCallback = widgetSettingsViewModel::onSpinnerValueChanged,
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
        quote.textSize = widgetParams.quoteTextSize
        quote.gravity = widgetParams.quoteTextGravity.resolveGravity()
        author.isVisible = widgetParams.isAuthorVisible
        if (widgetParams.isAuthorVisible) {
            author.gravity = widgetParams.quoteAuthorTextGravity.resolveGravity()
            author.textSize = widgetParams.quoteAuthorTextSize
        }
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

    private fun setupActionButton(state: ActionButtonState?) = state?.let {
        binding.actionButton.text = it.title
        binding.actionButton.setOnClickListener { _ -> it.onClickAction.invoke() }
        binding.actionButton.isEnabled = it.isEnabled
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    companion object {
        private const val BUNDLE_WIDGET_ID_KEY = "BUNDLE_WIDGET_ID_KEY"
        const val UNDEFINED_WIDGET_ID = WidgetSettingsViewModel.UNDEFINED_WIDGET_ID
        const val TAG = "WidgetSettingsFragment"
        fun newInstance(widgetId: Int) = WidgetSettingsFragment().apply {
            arguments = bundleOf(BUNDLE_WIDGET_ID_KEY to widgetId)
        }
    }
}