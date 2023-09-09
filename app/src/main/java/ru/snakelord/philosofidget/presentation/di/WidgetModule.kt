package ru.snakelord.philosofidget.presentation.di

import android.content.Context
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSource
import ru.snakelord.philosofidget.data.datasource.quote.QuoteDataSourceImpl
import ru.snakelord.philosofidget.data.datasource.quote.network.QuoteService
import ru.snakelord.philosofidget.data.repository.QuoteRepositoryImpl
import ru.snakelord.philosofidget.domain.interceptor.EscapingCharacterInterceptor
import ru.snakelord.philosofidget.domain.mapper.QuoteDTOMapper
import ru.snakelord.philosofidget.domain.model.GetQuoteParams
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.repository.QuoteRepository
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCaseWithParams
import ru.snakelord.philosofidget.domain.usecase.quote.GetKeyUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetStoredQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetUpdateTimeUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.RemoveStoredQuoteUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.StoreQuoteUseCase
import ru.snakelord.philosofidget.presentation.common.UseCases
import ru.snakelord.philosofidget.presentation.mapper.QuoteWidgetStateMapper
import ru.snakelord.philosofidget.presentation.widget.view_delegate.WidgetViewDelegate
import ru.snakelord.philosofidget.presentation.widget.view_delegate.WidgetViewDelegateImpl
import java.util.concurrent.TimeUnit

private const val RESPONSE_TIMEOUT = 10L
private const val CONTENT_TYPE = "application/json"
private const val BASE_API_URL = "http://api.forismatic.com/api/"
private const val QUOTE_WIDGET_SHARED_PREFS = "QUOTE_WIDGET_PREFS"

val widgetModule = module {

    factory { EscapingCharacterInterceptor() }

    factory {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val escapingCharacterInterceptor = inject<EscapingCharacterInterceptor>()

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(escapingCharacterInterceptor.value)
            .connectTimeout(RESPONSE_TIMEOUT, TimeUnit.SECONDS)
            .build()
        val contentType = CONTENT_TYPE.toMediaType()

        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_API_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(QuoteService::class.java)
    }

    single<SharedPreferences>(named(QUOTE_WIDGET_SHARED_PREFS)) {
        androidApplication().getSharedPreferences(
            QUOTE_WIDGET_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    factory<QuoteDataSource> { QuoteDataSourceImpl(get(), get(named(QUOTE_WIDGET_SHARED_PREFS))) }

    factory<QuoteRepository> { QuoteRepositoryImpl(get()) }

    factory { QuoteDTOMapper() }

    factory { QuoteWidgetStateMapper() }

    single<WidgetViewDelegate> { WidgetViewDelegateImpl(RemoteViews(androidApplication().packageName, R.layout.widget_quote)) }

    factory<CoroutineUseCaseWithParams<Quote, Unit>>(named(UseCases.STORE_QUOTE)) { StoreQuoteUseCase(get()) }

    factory<CoroutineUseCase<Unit>>(named(UseCases.REMOVE_STORED_QUOTE)) { RemoveStoredQuoteUseCase(get()) }

    factory<CoroutineUseCaseWithParams<GetQuoteParams, Quote>>(named(UseCases.GET_QUOTE)) { GetQuoteUseCase(get(), get()) }

    factory<CoroutineUseCase<Int>>(named(UseCases.GET_KEY)) { GetKeyUseCase() }

    factory<CoroutineUseCase<Quote?>>(named(UseCases.GET_STORED_QUOTE)) { GetStoredQuoteUseCase(get()) }

    factory<CoroutineUseCase<Long>>(named(UseCases.GET_UPDATE_TIME)) { GetUpdateTimeUseCase(get()) }
}