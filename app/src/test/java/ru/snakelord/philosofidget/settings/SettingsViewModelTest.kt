package ru.snakelord.philosofidget.settings

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CoroutineDispatcher
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
import kotlin.coroutines.CoroutineContext

/** Only IO-dispatched paths are exercised: Main-dispatched buttons require Android or coroutines-test. */
class SettingsViewModelTest {
    @Test fun pendingInitializationPublishesDefaultsThenLoadsStoredValues() = runBlocking {
        val fixture = SettingsFixture()
        val expected = QuoteWidgetParams(quoteLang = Lang.ENG, quoteTextSize = 19f, quoteUpdateTime = 4L)
        fixture.interactor.setNewWidgetParams(expected)
        val dispatcher = QueuedDispatcher()

        withViewModel(fixture, dispatcher = dispatcher) { viewModel ->
            assertEquals(QuoteWidgetParams(), viewModel.quoteWidgetParams.value)
            assertTrue(viewModel.widgetSettings.value.isEmpty())
            assertNull(viewModel.actionButtonState.value)
            assertEquals(WidgetSettingsViewModel.UNDEFINED_WIDGET_ID,
                viewModel.widgetConfigurationState.value.targetWidgetId)

            dispatcher.runPending()

            assertEquals(expected, viewModel.quoteWidgetParams.value)
            assertArrayEquals(fixture.interactor.getWidgetSettings(), viewModel.widgetSettings.value)
        }
    }

    @Test fun clearingViewModelCancelsPendingInitializationBeforeStorageIsRead() = runBlocking {
        val fixture = SettingsFixture()
        val dispatcher = QueuedDispatcher()
        val store = ViewModelStore()
        withViewModel(fixture, dispatcher = dispatcher, store = store) { viewModel ->
            // Any storage access after disposal is a failure, rather than a harmless late read.
            fixture.preferences.failure = IllegalStateException("disposed settings must not load")
            store.clear()
            dispatcher.runPending()

            assertEquals(QuoteWidgetParams(), viewModel.quoteWidgetParams.value)
            assertTrue(viewModel.widgetSettings.value.isEmpty())
            assertFalse(viewModel.widgetConfigurationState.value.isConfigurationSaved)
        }
    }

    @Test fun clearingViewModelCancelsQueuedEditsWithoutChangingPreviewOrStorage() = runBlocking {
        val fixture = SettingsFixture()
        val dispatcher = QueuedDispatcher()
        val store = ViewModelStore()
        withViewModel(fixture, dispatcher = dispatcher, store = store) { viewModel ->
            dispatcher.runPending()
            val original = viewModel.quoteWidgetParams.value
            viewModel.onToggleValueChanged(false, Toggle.ToggleTarget.AUTHOR_VISIBILITY)
            viewModel.onSliderValueChanged(11f, Slider.SliderTarget.QUOTE_TEXT_SIZE)

            store.clear()
            dispatcher.runPending()

            assertEquals(original, viewModel.quoteWidgetParams.value)
            assertEquals(original, fixture.interactor.getQuoteWidgetParams())
        }
    }

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
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        store: ViewModelStore = ViewModelStore(),
        block: suspend (WidgetSettingsViewModel) -> Unit
    ) {
        val manager = object : WidgetManager {
            override fun updateWidget(widgetPayloads: Set<WidgetPayload>) = error("Editing must not update widgets")
            override fun updateQuote() = error("Editing must not fetch quotes")
            override fun addWidgetOnHomeScreen() = error("Editing must not pin widgets")
            override fun hasActiveWidgets(): Boolean = error("No button setup expected")
        }
        // These dependencies complete synchronously; Unconfined needs neither a clock nor Main replacement.
        val viewModel = WidgetSettingsViewModel(fixture.interactor, dispatcher, manager, fixture.strings, targetId)
        store.put("settings", viewModel)
        try { block(viewModel) } finally { store.clear() }
    }

    /** Controls the injected IO boundary without real threads, sleeps, or a Main dispatcher. */
    private class QueuedDispatcher : CoroutineDispatcher() {
        private val pending = ArrayDeque<Runnable>()
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            pending.addLast(block)
        }
        fun runPending() {
            while (pending.isNotEmpty()) pending.removeFirst().run()
        }
    }
}
