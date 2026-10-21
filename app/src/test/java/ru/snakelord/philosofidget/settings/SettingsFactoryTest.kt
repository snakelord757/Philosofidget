package ru.snakelord.philosofidget.settings

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.model.WidgetSettings.*

class SettingsFactoryTest {
    @Test fun settingsExposeAllControlsWithLocalizedTitlesAndStoredValues() = runBlocking {
        val fixture = SettingsFixture()
        fixture.source.setAuthorVisibility(false)
        fixture.source.setQuoteLanguage("English")
        fixture.source.setQuoteTextGravity("Center")
        fixture.source.setQuoteAuthorTextGravity("Start")
        fixture.source.setQuoteTextSize(11f)
        fixture.source.setQuoteAuthorTextSize(12f)
        fixture.source.setWidgetUpdateTime(24L)
        fixture.source.setQuoteTextColor(0)
        fixture.source.setQuoteAuthorTextColor(-16777216)
        val gravityOptions = listOf("Start", "End", "Center")
        val expected = arrayOf<WidgetSettings>(
            Toggle(fixture.strings.getString(R.string.widget_settings_author_visibility_title), false, Toggle.ToggleTarget.AUTHOR_VISIBILITY),
            Spinner(fixture.strings.getString(R.string.widget_settings_quote_language), "English", listOf("Russian", "English"), Spinner.SpinnerTarget.LANGUAGE),
            Spinner(fixture.strings.getString(R.string.widget_settings_quote_text_gravity), "Center", gravityOptions, Spinner.SpinnerTarget.QUOTE_TEXT_GRAVITY),
            Spinner(fixture.strings.getString(R.string.widget_settings_quote_author_text_gravity), "Start", gravityOptions, Spinner.SpinnerTarget.QUOTE_AUTHOR_GRAVITY),
            Slider(fixture.strings.getString(R.string.widget_settings_quote_text_size), 11f, 11f, 30f, Slider.SliderTarget.QUOTE_TEXT_SIZE),
            Slider(fixture.strings.getString(R.string.widget_settings_quote_author_text_size), 12f, 11f, 15f, Slider.SliderTarget.QUOTE_AUTHOR_TEXT_SIZE),
            Slider(fixture.strings.getString(R.string.widget_settings_update_time), 24f, 1f, 24f, Slider.SliderTarget.QUOTE_UPDATE_TIME),
            ColorPicker(fixture.strings.getString(R.string.widget_settings_quote_text_color), 0, ColorPicker.ColorPickerTarget.QUOTE_TEXT_COLOR),
            ColorPicker(fixture.strings.getString(R.string.widget_settings_quote_author_text_color), -16777216, ColorPicker.ColorPickerTarget.QUOTE_AUTHOR_TEXT_COLOR)
        )
        assertArrayEquals(expected, fixture.interactor.getWidgetSettings())
    }

    @Test fun requestingSettingsAgainReflectsChangesWithoutMutatingPreviousSnapshot() = runBlocking {
        val fixture = SettingsFixture()
        val first = fixture.repository.getWidgetSettings()
        fixture.repository.setAuthorVisibility(false)
        val second = fixture.repository.getWidgetSettings()
        assertTrue((first[0] as Toggle).currentValue)
        assertFalse((second[0] as Toggle).currentValue)
        assertNotSame(first, second)
    }

    @Test fun factoryDoesNotHideStorageFailures() {
        val fixture = SettingsFixture()
        val failure = IllegalStateException("interval unavailable")
        fixture.preferences.failure = failure
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            runBlocking { fixture.repository.getWidgetSettings() }
        })
    }
}
