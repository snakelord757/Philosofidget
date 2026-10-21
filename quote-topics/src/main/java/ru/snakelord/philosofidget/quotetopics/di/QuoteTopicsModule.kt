package ru.snakelord.philosofidget.quotetopics.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.snakelord.philosofidget.quotetopics.data.*
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsInteractor
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsRepository
import ru.snakelord.philosofidget.quotetopics.presentation.QuoteTopicsViewModel

val quoteTopicsModule = module {
    single<QuoteTopicsDataSource> { MockQuoteTopicsDataSource() }
    single<TopicSelectionDataSource> {
        PreferencesTopicSelectionDataSource(
            androidContext().getSharedPreferences("QUOTE_TOPICS_PREFS", Context.MODE_PRIVATE)
        )
    }
    single<QuoteTopicsRepository> { QuoteTopicsRepositoryImpl(get(), get()) }
    factory { QuoteTopicsInteractor(get()) }
    viewModel { QuoteTopicsViewModel(get()) }
}
