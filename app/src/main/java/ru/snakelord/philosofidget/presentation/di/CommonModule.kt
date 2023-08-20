package ru.snakelord.philosofidget.presentation.di

import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.snakelord.philosofidget.domain.string_resolver.StringResolver
import ru.snakelord.philosofidget.domain.string_resolver.StringResolverImpl
import ru.snakelord.philosofidget.presentation.widget.WidgetUpdater

val commonModule = module {
    factory<StringResolver> { StringResolverImpl(androidApplication()) }

    factory { Dispatchers.IO }

    factory { WidgetUpdater(androidApplication()) }
}