package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.appwidget.AppWidgetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_AUTHOR_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Slider.SliderTarget.QUOTE_UPDATE_TIME
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Toggle.ToggleTarget
import ru.snakelord.philosofidget.presentation.model.WidgetConfigurationState
import ru.snakelord.philosofidget.presentation.widget.widget_updater.WidgetPayload
import ru.snakelord.philosofidget.presentation.widget.widget_updater.WidgetUpdater
import kotlin.math.roundToLong

class WidgetSettingsViewModel(
    private val widgetSettingsInteractor: WidgetSettingsInteractor,
    private val ioDispatcher: CoroutineDispatcher,
    private val widgetUpdater: WidgetUpdater,
    private val targetWidgetId: Int
) : ViewModel() {

    private val widgetSettingsStateFlow: MutableStateFlow<Array<WidgetSettings>> = MutableStateFlow(emptyArray())
    val widgetSettings = widgetSettingsStateFlow.asStateFlow()

    private val quoteWidgetParamsStateFlow: MutableStateFlow<QuoteWidgetParams> = MutableStateFlow(QuoteWidgetParams())
    val quoteWidgetParams = quoteWidgetParamsStateFlow.asStateFlow()

    private val widgetConfigurationStateFlow: MutableStateFlow<WidgetConfigurationState> =
        MutableStateFlow(WidgetConfigurationState(targetWidgetId = targetWidgetId, isConfigurationSaved = false))
    val widgetConfigurationState = widgetConfigurationStateFlow.asStateFlow()

    private val widgetPayloads = mutableSetOf<WidgetPayload>()

    fun loadQuoteWidgetParams() = viewModelScope.launch(ioDispatcher) {
        quoteWidgetParamsStateFlow.emit(widgetSettingsInteractor.getQuoteWidgetParams())
    }

    fun loadWidgetSettings() = viewModelScope.launch(ioDispatcher) {
        widgetSettingsStateFlow.emit(widgetSettingsInteractor.getWidgetSettings())
    }

    fun onToggleUpdated(newValue: Boolean, toggleTarget: ToggleTarget) = viewModelScope.launch(ioDispatcher) {
        when (toggleTarget) {
            ToggleTarget.AUTHOR_VISIBILITY -> {
                widgetSettingsInteractor.setAuthorVisibility(newValue)
                widgetPayloads.add(WidgetPayload.AUTHOR_VISIBILITY)
            }
        }
        loadQuoteWidgetParams()
    }

    fun onLanguageSelected(language: String) = viewModelScope.launch(ioDispatcher) {
        widgetSettingsInteractor.setQuoteLanguage(language)
        widgetPayloads.add(WidgetPayload.QUOTE_LANGUAGE)
    }

    fun onSliderValueChanged(newValue: Float, sliderTarget: WidgetSettings.Slider.SliderTarget) = viewModelScope.launch(ioDispatcher) {
        when (sliderTarget) {
            QUOTE_TEXT_SIZE -> {
                widgetSettingsInteractor.setQuoteTextSize(newValue)
                widgetPayloads.add(WidgetPayload.QUOTE_TEXT_SIZE)
            }

            QUOTE_AUTHOR_TEXT_SIZE -> {
                widgetSettingsInteractor.setQuoteAuthorTextSize(newValue)
                widgetPayloads.add(WidgetPayload.AUTHOR_TEXT_SIZE)
            }

            QUOTE_UPDATE_TIME -> {
                widgetSettingsInteractor.setWidgetUpdateTime(newValue.roundToLong())
                widgetPayloads.add(WidgetPayload.QUOTE_UPDATE_TIME)
            }
        }
        loadQuoteWidgetParams()
    }

    fun requestWidgetUpdate() = viewModelScope.launch {
        if (targetWidgetId != UNDEFINED_WIDGET_ID) {
            widgetConfigurationStateFlow.emit(widgetConfigurationStateFlow.value.copy(isConfigurationSaved = true))
        }
        widgetUpdater.updateWidget(widgetPayloads)
        widgetPayloads.clear()
    }

    companion object {
        const val UNDEFINED_WIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID
    }
}