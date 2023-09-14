package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.appwidget.AppWidgetManager
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
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_AUTHOR_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_UPDATE_TIME
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

    fun loadQuoteWidgetParams() = doOnIo { quoteWidgetParamsStateFlow.emit(widgetSettingsInteractor.getQuoteWidgetParams()) }

    fun loadWidgetSettings() = doOnIo { widgetSettingsStateFlow.emit(widgetSettingsInteractor.getWidgetSettings()) }

    fun setupButtonState() = viewModelScope.launch {
        val hasActiveWidgets = widgetManager.hasActiveWidgets()
        val (titleResId, onButtonClickAction) = if (hasActiveWidgets) {
            R.string.save_widget_configuration_button_text to ::requestWidgetUpdate
        } else {
            R.string.add_widget_on_home_screen_button_text to ::addWidgetOnHomeScreen
        }
        val isButtonEnabled = (titleResId == R.string.add_widget_on_home_screen_button_text || widgetPayloads.isNotEmpty() || isInConfigurationMode())
        actionButtonStateFlow.emit(
            ActionButtonState(
                title = stringResolver.getString(titleResId),
                onClickAction = onButtonClickAction,
                isEnabled = isButtonEnabled
            )
        )
    }

    fun onToggleUpdated(newValue: Boolean, toggleTarget: ToggleTarget) = doOnIo {
        when (toggleTarget) {
            ToggleTarget.AUTHOR_VISIBILITY -> {
                quoteWidgetParamsStateFlow.emit(quoteWidgetParams.value.copy(isAuthorVisible = newValue))
                widgetPayloads.add(WidgetPayload.AUTHOR_VISIBILITY)
            }
        }
        updateButtonEnabledState(isEnabled = true)
    }

    fun onLanguageSelected(language: String) = doOnIo {
        quoteWidgetParamsStateFlow.emit(quoteWidgetParams.value.copy(quoteLang = widgetSettingsInteractor.resolveLanguage(language)))
        widgetPayloads.add(WidgetPayload.QUOTE_LANGUAGE)
        updateButtonEnabledState(isEnabled = true)
    }

    fun onSliderValueChanged(newValue: Float, sliderTarget: WidgetSettings.Slider.SliderTarget) = doOnIo {
        when (sliderTarget) {
            QUOTE_TEXT_SIZE -> {
                quoteWidgetParamsStateFlow.emit(quoteWidgetParams.value.copy(quoteTextSize = newValue))
                widgetPayloads.add(WidgetPayload.QUOTE_TEXT_SIZE)
            }

            QUOTE_AUTHOR_TEXT_SIZE -> {
                quoteWidgetParamsStateFlow.emit(quoteWidgetParams.value.copy(quoteAuthorTextSize = newValue))
                widgetPayloads.add(WidgetPayload.AUTHOR_TEXT_SIZE)
            }

            QUOTE_UPDATE_TIME -> {
                quoteWidgetParamsStateFlow.emit(quoteWidgetParams.value.copy(quoteUpdateTime = newValue.toLong()))
                widgetPayloads.add(WidgetPayload.QUOTE_UPDATE_TIME)
            }
        }
        updateButtonEnabledState(isEnabled = true)
    }

    private fun requestWidgetUpdate() {
        doOnIo {
            if (isInConfigurationMode()) {
                widgetConfigurationStateFlow.emit(widgetConfigurationStateFlow.value.copy(isConfigurationSaved = true))
            }
            widgetSettingsInteractor.setNewWidgetParams(quoteWidgetParams.value)
            widgetManager.updateWidget(widgetPayloads)
            updateButtonEnabledState(isEnabled = false)
        }
    }

    private suspend fun updateButtonEnabledState(isEnabled: Boolean) {
        actionButtonStateFlow.emit(actionButtonStateFlow.value?.copy(isEnabled = isEnabled))
    }

    private fun addWidgetOnHomeScreen() {
        viewModelScope.launch { widgetManager.addWidgetOnHomeScreen() }
    }

    private fun doOnIo(block: suspend () -> Unit) = viewModelScope.launch(ioDispatcher) { block.invoke() }

    private fun isInConfigurationMode() = (targetWidgetId != UNDEFINED_WIDGET_ID)

    companion object {
        const val UNDEFINED_WIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID
    }
}