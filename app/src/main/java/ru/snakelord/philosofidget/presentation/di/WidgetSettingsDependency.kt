package ru.snakelord.philosofidget.presentation.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsDataSource
import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsDataSourceImpl
import ru.snakelord.philosofidget.data.repository.WidgetSettingsRepositoryImpl
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractorImpl
import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository
import ru.snakelord.philosofidget.presentation.view.widget_settings.WidgetSettingsViewModel

private const val QUOTE_WIDGET_SETTINGS_PREFS = "QUOTE_WIDGET_SETTINGS_PREFS"

val widgetSettingsModule = module {
    factory<SharedPreferences> { androidApplication().getSharedPreferences(QUOTE_WIDGET_SETTINGS_PREFS, Context.MODE_PRIVATE) }

    factory<WidgetSettingsDataSource> { WidgetSettingsDataSourceImpl(get()) }

    factory<WidgetSettingsRepository> { WidgetSettingsRepositoryImpl(get(), get()) }

    factory<WidgetSettingsInteractor> { WidgetSettingsInteractorImpl(get(), get()) }

    viewModel { (targetWidgetId: Int) -> WidgetSettingsViewModel(get(), get(), get(), targetWidgetId) }
}