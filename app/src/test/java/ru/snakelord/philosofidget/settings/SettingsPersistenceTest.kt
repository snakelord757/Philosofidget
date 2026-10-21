package ru.snakelord.philosofidget.settings

import android.content.SharedPreferences
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsDataSourceImpl
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsFactoryImpl
import ru.snakelord.philosofidget.data.repository.WidgetSettingsRepositoryImpl
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractorImpl
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.QuoteWidgetParams
import ru.snakelord.philosofidget.domain.model.TextGravity
import ru.snakelord.philosofidget.domain.usecase.quote.GetUpdateTimeUseCase
import ru.snakelord.philosofidget.domain.usecase.widget_settings.RemoveQuoteParamsUseCase

class SettingsPersistenceTest {
    @Test fun changingOneStoredSettingLeavesEveryOtherSettingUnchanged() = runBlocking {
        val fixture = SettingsFixture()
        val baseline = fixture.interactor.getQuoteWidgetParams()
        val changes: List<Pair<suspend () -> Unit, QuoteWidgetParams>> = listOf(
            (suspend { fixture.repository.setAuthorVisibility(false) }) to baseline.copy(isAuthorVisible = false),
            (suspend { fixture.repository.setQuoteLanguage("English") }) to baseline.copy(quoteLang = Lang.ENG),
            (suspend { fixture.repository.setQuoteTextSize(11f) }) to baseline.copy(quoteTextSize = 11f),
            (suspend { fixture.repository.setQuoteAuthorTextSize(12f) }) to baseline.copy(quoteAuthorTextSize = 12f),
            (suspend { fixture.repository.setWidgetUpdateTime(1L) }) to baseline.copy(quoteUpdateTime = 1L),
            (suspend { fixture.repository.setQuoteTextGravity("End") }) to baseline.copy(quoteTextGravity = TextGravity.END),
            (suspend { fixture.repository.setQuoteAuthorTextGravity("Center") }) to baseline.copy(quoteAuthorTextGravity = TextGravity.CENTER),
            (suspend { fixture.repository.setQuoteTextColor(0) }) to baseline.copy(quoteTextColor = 0),
            (suspend { fixture.repository.setQuoteAuthorTextColor(0x12345678) }) to baseline.copy(quoteAuthorTextColor = 0x12345678)
        )

        for ((change, expected) in changes) {
            fixture.interactor.setNewWidgetParams(baseline)
            change()
            assertEquals(expected, fixture.interactor.getQuoteWidgetParams())
        }
    }

    @Test fun emptyStorageProvidesAllDefaults() = runBlocking {
        val fixture = SettingsFixture()
        assertEquals(
            QuoteWidgetParams(quoteTextSize = 30f, quoteAuthorTextSize = 15f, quoteUpdateTime = 12L),
            fixture.interactor.getQuoteWidgetParams()
        )
    }

    @Test fun savingAndReloadingPreservesEveryParameterIndependently() = runBlocking {
        val fixture = SettingsFixture()
        val expected = QuoteWidgetParams(
            isAuthorVisible = false, quoteLang = Lang.ENG, quoteTextSize = 21.5f,
            quoteAuthorTextSize = 12.5f, quoteUpdateTime = 3L,
            quoteTextGravity = TextGravity.CENTER, quoteAuthorTextGravity = TextGravity.START,
            quoteTextColor = 0x12345678, quoteAuthorTextColor = -16777216
        )
        fixture.interactor.setNewWidgetParams(expected)
        assertEquals(expected, fixture.interactor.getQuoteWidgetParams())
        assertEquals("English", fixture.source.getQuoteLanguage())
        assertEquals("Center", fixture.source.getQuoteTextGravity())
        assertEquals("Start", fixture.source.getQuoteAuthorTextGravity())
    }

    @Test fun repeatedSaveReplacesPreviousValues() = runBlocking {
        val fixture = SettingsFixture()
        fixture.interactor.setNewWidgetParams(QuoteWidgetParams(quoteLang = Lang.ENG, isAuthorVisible = false))
        val expected = QuoteWidgetParams(quoteTextSize = 11f, quoteAuthorTextSize = 15f, quoteUpdateTime = 24L)
        fixture.interactor.setNewWidgetParams(expected)
        assertEquals(expected, fixture.interactor.getQuoteWidgetParams())
    }

    @Test fun recreatedSettingsLayersRestoreAllSavedParameters() = runBlocking {
        val fixture = SettingsFixture()
        val expected = QuoteWidgetParams(
            false, Lang.ENG, 17.5f, 12f, 6L, TextGravity.END, TextGravity.CENTER,
            0x12345678, -16777216
        )
        fixture.interactor.setNewWidgetParams(expected)

        val recreatedSource = WidgetSettingsDataSourceImpl(fixture.preferences, fixture.strings)
        val recreatedFactory = WidgetSettingsFactoryImpl(recreatedSource, fixture.strings)
        val recreatedRepository = WidgetSettingsRepositoryImpl(recreatedSource, recreatedFactory)
        val recreatedInteractor = WidgetSettingsInteractorImpl(recreatedRepository, fixture.strings)

        assertEquals(expected, recreatedInteractor.getQuoteWidgetParams())
        assertArrayEquals(fixture.interactor.getWidgetSettings(), recreatedInteractor.getWidgetSettings())
    }

    @Test fun nullablePreferenceStringsUseLocalizedDefaults() = runBlocking {
        val fixture = SettingsFixture()
        val nullablePreferences = object : SharedPreferences by fixture.preferences {
            override fun getString(key: String?, defValue: String?): String? = null
        }
        val source = WidgetSettingsDataSourceImpl(nullablePreferences, fixture.strings)

        assertEquals("Russian", source.getQuoteLanguage())
        assertEquals("Start", source.getQuoteTextGravity())
        assertEquals("End", source.getQuoteAuthorTextGravity())
    }

    @Test fun allLanguagesAndGravitiesRoundTrip() = runBlocking {
        val fixture = SettingsFixture()
        for (language in Lang.values()) for (gravity in TextGravity.values()) {
            val expected = QuoteWidgetParams(quoteLang = language, quoteTextGravity = gravity, quoteAuthorTextGravity = gravity)
            fixture.interactor.setNewWidgetParams(expected)
            assertEquals(expected, fixture.interactor.getQuoteWidgetParams())
        }
    }

    @Test fun unknownLocalizedValuesFallBackToRussianAndStart() = runBlocking {
        val fixture = SettingsFixture()
        fixture.source.setQuoteLanguage("unsupported")
        fixture.source.setQuoteTextGravity("")
        fixture.source.setQuoteAuthorTextGravity("obsolete translation")
        val params = fixture.interactor.getQuoteWidgetParams()
        assertEquals(Lang.RU, params.quoteLang)
        assertEquals(TextGravity.START, params.quoteTextGravity)
        assertEquals(TextGravity.START, params.quoteAuthorTextGravity)
    }

    @Test fun removingParametersRestoresEveryDefaultAndIsIdempotent() = runBlocking {
        val fixture = SettingsFixture()
        val defaults = fixture.interactor.getQuoteWidgetParams()
        fixture.interactor.setNewWidgetParams(QuoteWidgetParams(
            false, Lang.ENG, 11f, 11f, 1L, TextGravity.CENTER, TextGravity.CENTER, 0, 0
        ))
        val remove = RemoveQuoteParamsUseCase(fixture.interactor)
        remove.invoke()
        assertEquals(defaults, fixture.interactor.getQuoteWidgetParams())
        assertTrue(fixture.preferences.all.isEmpty())
        remove.invoke()
        assertEquals(defaults, fixture.interactor.getQuoteWidgetParams())
    }

    @Test fun updateTimeUseCaseReadsLatestStoredInterval() = runBlocking {
        val fixture = SettingsFixture()
        val getUpdateTime = GetUpdateTimeUseCase(fixture.repository)
        assertEquals(12L, getUpdateTime.invoke())
        fixture.repository.setWidgetUpdateTime(1L)
        assertEquals(1L, getUpdateTime.invoke())
        fixture.repository.setWidgetUpdateTime(24L)
        assertEquals(24L, getUpdateTime.invoke())
    }

    @Test fun storageReadErrorsReachUpdateTimeUseCaseAndInteractor() {
        val fixture = SettingsFixture()
        val failure = IllegalStateException("storage read failed")
        fixture.preferences.failure = failure
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            runBlocking { GetUpdateTimeUseCase(fixture.repository).invoke() }
        })
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            runBlocking { fixture.interactor.getQuoteWidgetParams() }
        })
    }

    @Test fun storageWriteErrorsReachSaveAndRemoveUseCase() {
        val fixture = SettingsFixture()
        val failure = IllegalStateException("storage write failed")
        fixture.preferences.failure = failure
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            runBlocking { fixture.interactor.setNewWidgetParams(QuoteWidgetParams()) }
        })
        assertSame(failure, assertThrows(IllegalStateException::class.java) {
            runBlocking { RemoveQuoteParamsUseCase(fixture.interactor).invoke() }
        })
        assertTrue(fixture.preferences.all.isEmpty())
    }
}
