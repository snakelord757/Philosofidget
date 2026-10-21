package ru.snakelord.philosofidget.settings

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.TextGravity
import ru.snakelord.philosofidget.domain.model.WidgetSettings.*
import ru.snakelord.philosofidget.presentation.view.widget_settings.WidgetSettingsViewModel
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetManager
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetPayload

/** Only IO-dispatched paths are exercised: Main-dispatched buttons require Android or coroutines-test. */
class SettingsViewModelTest {
    @Test fun initializationLoadsPersistedSettingsAndConfigurationState() = runBlocking {
        val fixture = SettingsFixture()
        val expected = QuoteWidgetParams(quoteLang = Lang.ENG, quoteUpdateTime = 6L, quoteTextSize = 20f)
        fixture.interactor.setNewWidgetParams(expected)
        withViewModel(fixture, targetId = 42) { viewModel ->
            assertEquals(expected, viewModel.quoteWidgetParams.value)
            assertArrayEquals(fixture.interactor.getWidgetSettings(), viewModel.widgetSettings.value)
            assertEquals(42, viewModel.widgetConfigurationState.value.targetWidgetId)
            assertFalse(viewModel.widgetConfigurationState.value.isConfigurationSaved)
            assertNull(viewModel.actionButtonState.value)
        }
    }

    @Test fun everyEditorUpdatesItsParameterWithoutSavingChanges() = runBlocking {
        val fixture = SettingsFixture()
        val stored = fixture.interactor.getQuoteWidgetParams()
        withViewModel(fixture) { viewModel ->
            viewModel.onToggleValueChanged(false, Toggle.ToggleTarget.AUTHOR_VISIBILITY)
            assertEquals(stored.copy(isAuthorVisible = false), viewModel.quoteWidgetParams.value)

            viewModel.onSpinnerValueChanged("English", Spinner.SpinnerTarget.LANGUAGE)
            viewModel.onSpinnerValueChanged("Center", Spinner.SpinnerTarget.QUOTE_TEXT_GRAVITY)
            viewModel.onSpinnerValueChanged("Start", Spinner.SpinnerTarget.QUOTE_AUTHOR_GRAVITY)
            viewModel.onSliderValueChanged(11f, Slider.SliderTarget.QUOTE_TEXT_SIZE)
            viewModel.onSliderValueChanged(13.5f, Slider.SliderTarget.QUOTE_AUTHOR_TEXT_SIZE)
            viewModel.onSliderValueChanged(24f, Slider.SliderTarget.QUOTE_UPDATE_TIME)
            viewModel.onColorPicked(ColorPicker.ColorPickerTarget.QUOTE_TEXT_COLOR, 0)
            viewModel.onColorPicked(ColorPicker.ColorPickerTarget.QUOTE_AUTHOR_TEXT_COLOR, -16777216)

            assertEquals(QuoteWidgetParams(false, Lang.ENG, 11f, 13.5f, 24L,
                TextGravity.CENTER, TextGravity.START, 0, -16777216), viewModel.quoteWidgetParams.value)
            assertEquals(stored, fixture.interactor.getQuoteWidgetParams())
            assertFalse(viewModel.widgetConfigurationState.value.isConfigurationSaved)
        }
    }

    @Test fun subsequentEditsReplaceValuesAndResolveUnknownSpinnerOptions() = runBlocking {
        val fixture = SettingsFixture()
        withViewModel(fixture) { viewModel ->
            viewModel.onSpinnerValueChanged("English", Spinner.SpinnerTarget.LANGUAGE)
            viewModel.onSpinnerValueChanged("unknown", Spinner.SpinnerTarget.LANGUAGE)
            viewModel.onSpinnerValueChanged("unknown", Spinner.SpinnerTarget.QUOTE_AUTHOR_GRAVITY)
            viewModel.onSliderValueChanged(11f, Slider.SliderTarget.QUOTE_TEXT_SIZE)
            viewModel.onSliderValueChanged(30f, Slider.SliderTarget.QUOTE_TEXT_SIZE)
            assertEquals(Lang.RU, viewModel.quoteWidgetParams.value.quoteLang)
            assertEquals(TextGravity.START, viewModel.quoteWidgetParams.value.quoteAuthorTextGravity)
            assertEquals(30f, viewModel.quoteWidgetParams.value.quoteTextSize, 0f)
        }
    }

    private suspend fun withViewModel(
        fixture: SettingsFixture,
        targetId: Int = WidgetSettingsViewModel.UNDEFINED_WIDGET_ID,
        block: suspend (WidgetSettingsViewModel) -> Unit
    ) {
        val manager = object : WidgetManager {
            override fun updateWidget(widgetPayloads: Set<WidgetPayload>) = error("Editing must not update widgets")
            override fun updateQuote() = error("Editing must not fetch quotes")
            override fun addWidgetOnHomeScreen() = error("Editing must not pin widgets")
            override fun hasActiveWidgets(): Boolean = error("No button setup expected")
        }
        // These dependencies complete synchronously; Unconfined needs neither a clock nor Main replacement.
        val viewModel = WidgetSettingsViewModel(fixture.interactor, Dispatchers.Unconfined, manager, fixture.strings, targetId)
        val store = ViewModelStore()
        store.put("settings", viewModel)
        try { block(viewModel) } finally { store.clear() }
    }
}
