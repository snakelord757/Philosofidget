package ru.snakelord.philosofidget.presentation.di

import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver
import ru.snakelord.philosofidget.domain.string_resolver.StringResolverImpl
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetManager
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetManagerImpl

val commonModule = module {
    single<StringResolver> { StringResolverImpl(androidApplication().resources) }

    factory { Dispatchers.IO }

    single<WidgetManager> {
        WidgetManagerImpl(
            context = androidApplication(),
            stringResolver = get()
        )
    }
}