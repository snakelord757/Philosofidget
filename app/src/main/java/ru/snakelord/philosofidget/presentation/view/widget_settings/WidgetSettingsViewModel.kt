package ru.snakelord.philosofidget.presentation.view.widget_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.model.WidgetSettings.Toggle.ToggleTarget
import ru.snakelord.philosofidget.presentation.widget.WidgetUpdater

class WidgetSettingsViewModel(
    private val widgetSettingsInteractor: WidgetSettingsInteractor,
    private val ioDispatcher: CoroutineDispatcher,
    private val widgetUpdater: WidgetUpdater
) : ViewModel() {

    private val widgetSettingsStateFlow: MutableStateFlow<Array<WidgetSettings>> = MutableStateFlow(emptyArray())
    val widgetSettings = widgetSettingsStateFlow.asStateFlow()

    private val quoteWidgetParamsStateFlow: MutableStateFlow<QuoteWidgetParams> = MutableStateFlow(QuoteWidgetParams())
    val quoteWidgetParams = quoteWidgetParamsStateFlow.asStateFlow()

    fun onToggleUpdated(newValue: Boolean, toggleTarget: ToggleTarget) {
        when (toggleTarget) {
            ToggleTarget.AUTHOR_VISIBILITY -> widgetSettingsInteractor.setAuthorVisibility(newValue)
        }
        loadQuoteWidgetParams()
    }

    fun onLanguageSelected(language: String) {
        widgetSettingsInteractor.setQuoteLanguage(language)
    }

    fun requestWidgetUpdate() = widgetUpdater.updateWidget()

    fun loadQuoteWidgetParams() = viewModelScope.launch(ioDispatcher) {
        quoteWidgetParamsStateFlow.emit(widgetSettingsInteractor.getQuoteWidgetParams())
    }

    fun loadWidgetSettings() = viewModelScope.launch(ioDispatcher) {
        widgetSettingsStateFlow.emit(widgetSettingsInteractor.getWidgetSettings())
    }
}