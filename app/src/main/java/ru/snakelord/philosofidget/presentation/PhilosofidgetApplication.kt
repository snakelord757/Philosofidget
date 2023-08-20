package ru.snakelord.philosofidget.presentation

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.snakelord.philosofidget.presentation.di.commonModule
import ru.snakelord.philosofidget.presentation.di.widgetModule
import ru.snakelord.philosofidget.presentation.di.widgetSettingsModule

class PhilosofidgetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhilosofidgetApplication)
            modules(commonModule, widgetModule, widgetSettingsModule)
        }
    }
}