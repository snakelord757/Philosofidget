package ru.snakelord.philosofidget.presentation.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSource
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSourceImpl
import ru.snakelord.philosofidget.data.datasource.quote.network.QuoteService
import ru.snakelord.philosofidget.data.repository.QuoteRepositoryImpl
import ru.snakelord.philosofidget.domain.mapper.QuoteDTOMapper
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.quote.GetKeyUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetQuoteUseCase
import ru.snakelord.philosofidget.presentation.mapper.QuoteWidgetStateMapper
import ru.snakelord.philosofidget.presentation.notification_builder.NotificationProvider
import ru.snakelord.philosofidget.presentation.notification_builder.NotificationProviderImpl
import ru.snakelord.philosofidget.presentation.service.QuoteViewModel
import java.util.concurrent.TimeUnit

private const val RESPONSE_TIMEOUT = 10L

val widgetModule = module {
    factory {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(RESPONSE_TIMEOUT, TimeUnit.SECONDS)
            .build()
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .client(client)
            .baseUrl("http://api.forismatic.com/api/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(QuoteService::class.java)
    }

    factory<QuoteDataSource> { QuoteDataSourceImpl(get()) }

    factory<QuoteRepository> { QuoteRepositoryImpl(get()) }

    factory { QuoteDTOMapper() }

    factory { QuoteWidgetStateMapper() }

    factory { GetQuoteUseCase(get(), get()) }

    factory { GetKeyUseCase() }

    factory<NotificationProvider> { NotificationProviderImpl(get(), androidApplication()) }

    viewModel { QuoteViewModel(get(), get(), get(), get(), get(), get()) }
}