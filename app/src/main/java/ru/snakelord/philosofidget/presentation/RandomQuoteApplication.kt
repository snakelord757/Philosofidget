package ru.snakelord.philosofidget.presentation

import android.app.Application
import org.koin.core.context.startKoin
import ru.snakelord.philosofidget.presentation.di.dependencyModule

class RandomQuoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { modules(dependencyModule) }
    }
}