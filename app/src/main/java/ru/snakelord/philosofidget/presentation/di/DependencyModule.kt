package ru.snakelord.philosofidget.presentation.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSource
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSourceImpl
import ru.snakelord.philosofidget.data.datasource.quote.network.QuoteService
import ru.snakelord.philosofidget.data.repository.QuoteRepositoryImpl
import ru.snakelord.philosofidget.domain.mapper.quoteDtoMapper
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.quote.GetKeyUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetQuoteUseCase
import ru.snakelord.philosofidget.presentation.mapper.quoteMapper
import ru.snakelord.philosofidget.presentation.service.QuoteViewModel
import java.util.concurrent.TimeUnit

private const val QUOTE_DTO_MAPPER = "QUOTE_DTO_MAPPER"
private const val QUOTE_WIDGET_STATE_MAPPER = "QUOTE_WIDGET_STATE_MAPPER"
private const val RESPONSE_TIMEOUT = 10L

val dependencyModule = module {
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

    factory(named(QUOTE_DTO_MAPPER)) { quoteDtoMapper }

    factory(named(QUOTE_WIDGET_STATE_MAPPER)) { quoteMapper }

    factory { GetQuoteUseCase(get(), get(named(QUOTE_DTO_MAPPER))) }

    factory { Dispatchers.IO }

    factory { GetKeyUseCase() }

    viewModel { QuoteViewModel(get(), get(named(QUOTE_WIDGET_STATE_MAPPER)), get(), get()) }
}