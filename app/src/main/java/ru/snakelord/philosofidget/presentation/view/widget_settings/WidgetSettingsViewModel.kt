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
import kotlin.math.roundToLong

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

    private val actionButtonStateSharedFlow: MutableStateFlow<ActionButtonState?> = MutableStateFlow(null)
    val actionButtonState = actionButtonStateSharedFlow.asStateFlow()

    private val widgetPayloads = mutableSetOf<WidgetPayload>()

    fun loadQuoteWidgetParams() = doOnIo { quoteWidgetParamsStateFlow.emit(widgetSettingsInteractor.getQuoteWidgetParams()) }

    fun loadWidgetSettings() = doOnIo { widgetSettingsStateFlow.emit(widgetSettingsInteractor.getWidgetSettings()) }

    fun setupButtonState() = viewModelScope.launch {
        val hasActiveWidgets = widgetManager.hasActiveWidgets()
        val (title: String, onButtonClickAction) = if (hasActiveWidgets) {
            stringResolver.getString(R.string.save_widget_configuration_button_text) to ::requestWidgetUpdate
        } else {
            stringResolver.getString(R.string.add_widget_on_home_screen_button_text) to ::addWidgetOnHomeScreen
        }
        actionButtonStateSharedFlow.emit(ActionButtonState(title, onButtonClickAction))
    }

    fun onToggleUpdated(newValue: Boolean, toggleTarget: ToggleTarget) = doOnIo {
        when (toggleTarget) {
            ToggleTarget.AUTHOR_VISIBILITY -> {
                widgetSettingsInteractor.setAuthorVisibility(newValue)
                widgetPayloads.add(WidgetPayload.AUTHOR_VISIBILITY)
            }
        }
        loadQuoteWidgetParams()
    }

    fun onLanguageSelected(language: String) = doOnIo {
        widgetSettingsInteractor.setQuoteLanguage(language)
        widgetPayloads.add(WidgetPayload.QUOTE_LANGUAGE)
    }

    fun onSliderValueChanged(newValue: Float, sliderTarget: WidgetSettings.Slider.SliderTarget) = doOnIo {
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

    private fun requestWidgetUpdate() {
        viewModelScope.launch {
            if (targetWidgetId != UNDEFINED_WIDGET_ID) {
                widgetConfigurationStateFlow.emit(widgetConfigurationStateFlow.value.copy(isConfigurationSaved = true))
            }
            widgetManager.updateWidget(widgetPayloads)
            widgetPayloads.clear()
        }
    }

    private fun addWidgetOnHomeScreen() {
        viewModelScope.launch { widgetManager.addWidgetOnHomeScreen() }
    }

    private fun doOnIo(block: suspend () -> Unit) = viewModelScope.launch(ioDispatcher) { block.invoke() }

    companion object {
        const val UNDEFINED_WIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID
    }
}