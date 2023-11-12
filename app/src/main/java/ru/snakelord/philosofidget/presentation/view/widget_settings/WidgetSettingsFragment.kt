package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.abhishek.colorpicker.ColorPickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.databinding.FragmentWidgetSettingsBinding
import ru.snakelord.philosofidget.domain.ext.subscribeOnLifecycle
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.TextGravity
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
        parametersOf(requireArguments().getInt(BUNDLE_WIDGET_ID_KEY, UNDEFINED_WIDGET_ID))
    }

    private val widgetSettingsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WidgetSettingsAdapter(
            toggleCallback = widgetSettingsViewModel::onToggleValueChanged,
            spinnerCallback = widgetSettingsViewModel::onSpinnerValueChanged,
            sliderCallback = widgetSettingsViewModel::onSliderValueChanged,
            openColorPickerCallback = ::openColorPickerDialog
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
        widgetSettings.subscribeOnLifecycle(viewLifecycleOwner, ::setupWidgetSettings)
        quoteWidgetParams.subscribeOnLifecycle(viewLifecycleOwner, ::updateWidgetPreview)
        widgetConfigurationState.subscribeOnLifecycle(viewLifecycleOwner, ::onConfigurationSaved)
        actionButtonState.subscribeOnLifecycle(viewLifecycleOwner) { if (it != null) setupActionButton(it) }
    }

    private fun openColorPickerDialog(colorPickerTarget: WidgetSettings.ColorPicker.ColorPickerTarget) {
        ColorPickerDialog().apply {
            setOnOkCancelListener { isOk, color ->
                if (isOk) widgetSettingsViewModel.onColorPicked(colorPickerTarget, color)
            }
        }
            .show(parentFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        widgetSettingsViewModel.setupButtonState()
    }

    private fun updateWidgetPreview(widgetParams: QuoteWidgetParams) = with(binding.quoteWidgetPreview) {
        quote.textSize = widgetParams.quoteTextSize
        quote.gravity = resolveAndroidGravity(widgetParams.quoteTextGravity)
        quote.setTextColor(widgetParams.quoteTextColor)
        author.isVisible = widgetParams.isAuthorVisible
        author.setTextColor(widgetParams.quoteAuthorTextColor)
        if (widgetParams.isAuthorVisible) {
            author.gravity = resolveAndroidGravity(widgetParams.quoteAuthorTextGravity)
            author.textSize = widgetParams.quoteAuthorTextSize
        }
    }

    private fun resolveAndroidGravity(textGravity: TextGravity): Int = when (textGravity) {
        TextGravity.START -> Gravity.START
        TextGravity.END -> Gravity.END
        TextGravity.CENTER -> Gravity.CENTER
    }

    private fun setupWidgetSettings(widgetSettings: Array<WidgetSettings>) {
        widgetSettingsAdapter.setSettings(widgetSettings)
    }

    private fun onConfigurationSaved(widgetConfigurationState: WidgetConfigurationState) {
        val resultIntent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetConfigurationState.targetWidgetId)
        val resultCode = if (widgetConfigurationState.isConfigurationSaved) Activity.RESULT_OK else Activity.RESULT_CANCELED
        requireActivity().apply {
            setResult(resultCode, resultIntent)
            if (widgetConfigurationState.isConfigurationSaved) finish()
        }
    }

    private fun setupActionButton(state: ActionButtonState) = with(binding.actionButton) {
        text = state.title
        setOnClickListener { _ -> state.onClickAction.invoke() }
        isEnabled = state.isEnabled
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