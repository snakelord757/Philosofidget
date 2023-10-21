package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.appwidget.AppWidgetManager
import androidx.annotation.ColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.model.WidgetSettings.ColorPicker.ColorPickerTarget.QUOTE_AUTHOR_TEXT_COLOR
import ru.snakelord.philosofidget.domain.model.WidgetSettings.ColorPicker.ColorPickerTarget.QUOTE_TEXT_COLOR
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_AUTHOR_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_UPDATE_TIME
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Spinner.SpinnerTarget.LANGUAGE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Spinner.SpinnerTarget.QUOTE_AUTHOR_GRAVITY
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Spinner.SpinnerTarget.QUOTE_TEXT_GRAVITY
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Toggle.ToggleTarget
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver
import ru.snakelord.philosofidget.presentation.model.ActionButtonState
import ru.snakelord.philosofidget.presentation.model.WidgetConfigurationState
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetManager
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetPayload

class WidgetSettingsViewModel(
    private val widgetSettingsInteractor: WidgetSettingsInteractor,
    private val ioDispatcher: CoroutineDispatcher,
    private val widgetManager: WidgetManager,
    private val stringResolver: StringResolver,
    private val targetWidgetId: Int
) : ViewModel() {

    private val widgetSettingsStateFlow: MutableStateFlow<Array<WidgetSettings>> = MutableStateFlow(emptyArray())
    val widgetSettings = widgetSettingsStateFlow.asStateFlow()

    private val quoteWidgetParamsStateFlow: MutableStateFlow<QuoteWidgetParams> = MutableStateFlow(QuoteWidgetParams())
    val quoteWidgetParams = quoteWidgetParamsStateFlow.asStateFlow()

    private val widgetConfigurationStateFlow: MutableStateFlow<WidgetConfigurationState> =
        MutableStateFlow(WidgetConfigurationState(targetWidgetId = targetWidgetId, isConfigurationSaved = false))
    val widgetConfigurationState = widgetConfigurationStateFlow.asStateFlow()

    private val actionButtonStateFlow: MutableStateFlow<ActionButtonState?> = MutableStateFlow(null)
    val actionButtonState = actionButtonStateFlow.asStateFlow()

    private val widgetPayloads = mutableSetOf<WidgetPayload>()

    init {
        doOnIo {
            loadQuoteWidgetParams()
            loadWidgetSettings()
        }
    }

    private suspend fun loadQuoteWidgetParams() = quoteWidgetParamsStateFlow.emit(widgetSettingsInteractor.getQuoteWidgetParams())

    private suspend fun loadWidgetSettings() = widgetSettingsStateFlow.emit(widgetSettingsInteractor.getWidgetSettings())

    fun setupButtonState() = viewModelScope.launch {
        val hasActiveWidgets = widgetManager.hasActiveWidgets()
        val (titleResId, onButtonClickAction) = if (hasActiveWidgets) {
            R.string.save_widget_configuration_button_text to ::requestWidgetUpdate
        } else {
            R.string.add_widget_on_home_screen_button_text to ::addWidgetOnHomeScreen
        }
        val isButtonEnabled = (hasActiveWidgets.not() || widgetPayloads.isNotEmpty() || isInConfigurationMode())
        actionButtonStateFlow.emit(
            ActionButtonState(
                title = stringResolver.getString(titleResId),
                onClickAction = onButtonClickAction,
                isEnabled = isButtonEnabled
            )
        )
    }

    fun onToggleValueChanged(newValue: Boolean, toggleTarget: ToggleTarget) = updateWidgetParams {
        when (toggleTarget) {
            ToggleTarget.AUTHOR_VISIBILITY -> {
                widgetPayloads.add(WidgetPayload.AUTHOR_VISIBILITY)
                copy(isAuthorVisible = newValue)
            }
        }
    }

    fun onSpinnerValueChanged(newValue: String, spinnerTarget: WidgetSettings.Spinner.SpinnerTarget) = updateWidgetParams {
        when (spinnerTarget) {
            LANGUAGE -> {
                widgetPayloads.add(WidgetPayload.QUOTE_LANGUAGE)
                copy(quoteLang = widgetSettingsInteractor.resolveLanguage(newValue))
            }

            QUOTE_TEXT_GRAVITY -> {
                widgetPayloads.add(WidgetPayload.QUOTE_TEXT_GRAVITY)
                copy(quoteTextGravity = widgetSettingsInteractor.resolveGravity(newValue))
            }

            QUOTE_AUTHOR_GRAVITY -> {
                widgetPayloads.add(WidgetPayload.QUOTE_AUTHOR_TEXT_GRAVITY)
                copy(quoteAuthorTextGravity = widgetSettingsInteractor.resolveGravity(newValue))
            }
        }
    }

    fun onSliderValueChanged(newValue: Float, sliderTarget: WidgetSettings.Slider.SliderTarget) = updateWidgetParams {
        when (sliderTarget) {
            QUOTE_TEXT_SIZE -> {
                widgetPayloads.add(WidgetPayload.QUOTE_TEXT_SIZE)
                copy(quoteTextSize = newValue)
            }

            QUOTE_AUTHOR_TEXT_SIZE -> {
                widgetPayloads.add(WidgetPayload.AUTHOR_TEXT_SIZE)
                copy(quoteAuthorTextSize = newValue)
            }

            QUOTE_UPDATE_TIME -> {
                widgetPayloads.add(WidgetPayload.QUOTE_UPDATE_TIME)
                copy(quoteUpdateTime = newValue.toLong())
            }
        }
    }

    fun onColorPicked(colorPickerTarget: WidgetSettings.ColorPicker.ColorPickerTarget, @ColorInt color: Int) = updateWidgetParams{
        when (colorPickerTarget) {
            QUOTE_TEXT_COLOR -> {
                widgetPayloads.add(WidgetPayload.QUOTE_TEXT_COLOR)
                copy(quoteTextColor = color)
            }

            QUOTE_AUTHOR_TEXT_COLOR -> {
                widgetPayloads.add(WidgetPayload.QUOTE_AUTHOR_TEXT_COLOR)
                copy(quoteAuthorTextColor = color)
            }
        }
    }

    private fun requestWidgetUpdate() = doOnIo {
        val isInConfigurationMode = isInConfigurationMode()
        if (isInConfigurationMode || !widgetManager.hasActiveWidgets()) widgetPayloads.add(WidgetPayload.QUOTE)
        widgetSettingsInteractor.setNewWidgetParams(quoteWidgetParams.value)
        updateButtonEnabledState(isEnabled = false)
        widgetManager.updateWidget(widgetPayloads)
        if (isInConfigurationMode) widgetConfigurationStateFlow.emit(widgetConfigurationStateFlow.value.copy(isConfigurationSaved = true))
    }

    private suspend fun updateButtonEnabledState(isEnabled: Boolean) = doOnIo {
        actionButtonStateFlow.emit(actionButtonStateFlow.value?.copy(isEnabled = isEnabled))
    }

    private fun updateWidgetParams(updateBlock: QuoteWidgetParams.() -> QuoteWidgetParams) = doOnIo {
        quoteWidgetParamsStateFlow.emit(quoteWidgetParamsStateFlow.value.updateBlock())
        updateButtonEnabledState(isEnabled = true)
    }

    private fun addWidgetOnHomeScreen() {
        viewModelScope.launch { widgetManager.addWidgetOnHomeScreen() }
    }

    private fun doOnIo(block: suspend () -> Unit) {
        viewModelScope.launch(ioDispatcher) { block.invoke() }
    }

    private fun isInConfigurationMode() = (targetWidgetId != UNDEFINED_WIDGET_ID)

    companion object {
        const val UNDEFINED_WIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID
    }
}