package ru.snakelord.philosofidget.presentation.view.widget_settings

import android.appwidget.AppWidgetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.model.WidgetSettings.SeekBar.SeekBarTarget.QUOTE_AUTHOR_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.SeekBar.SeekBarTarget.QUOTE_TEXT_SIZE
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Toggle.ToggleTarget
import ru.snakelord.philosofidget.presentation.model.WidgetConfigurationState
import ru.snakelord.philosofidget.presentation.widget.WidgetUpdater

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

    val widgetConfigurationState: StateFlow<WidgetConfigurationState> = widgetConfigurationStateFlow.asStateFlow()

    fun loadQuoteWidgetParams() = viewModelScope.launch(ioDispatcher) {
        quoteWidgetParamsStateFlow.emit(widgetSettingsInteractor.getQuoteWidgetParams())
    }

    fun loadWidgetSettings() = viewModelScope.launch(ioDispatcher) {
        widgetSettingsStateFlow.emit(widgetSettingsInteractor.getWidgetSettings())
    }

    fun onToggleUpdated(newValue: Boolean, toggleTarget: ToggleTarget) {
        when (toggleTarget) {
            ToggleTarget.AUTHOR_VISIBILITY -> widgetSettingsInteractor.setAuthorVisibility(newValue)
        }
        loadQuoteWidgetParams()
    }

    fun onLanguageSelected(language: String) {
        widgetSettingsInteractor.setQuoteLanguage(language)
    }

    fun onSliderValueChanged(newValue: Float, seekBarTarget: WidgetSettings.SeekBar.SeekBarTarget) {
        when (seekBarTarget) {
            QUOTE_TEXT_SIZE -> widgetSettingsInteractor.setQuoteTextSize(newValue)
            QUOTE_AUTHOR_TEXT_SIZE -> widgetSettingsInteractor.setQuoteAuthorTextSize(newValue)
        }
        loadQuoteWidgetParams()
    }

    fun requestWidgetUpdate() {
        if (targetWidgetId != UNDEFINED_WIDGET_ID) {
            viewModelScope.launch {
                widgetConfigurationStateFlow.emit(widgetConfigurationStateFlow.value.copy(isConfigurationSaved = true))
            }
        }
        widgetUpdater.updateWidget()
    }

    companion object {
        const val UNDEFINED_WIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID
    }
}