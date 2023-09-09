package ru.snakelord.philosofidget.presentation.widget.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.model.GetQuoteParams
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCaseWithParams
import ru.snakelord.philosofidget.presentation.common.UseCases
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetManager
import java.util.concurrent.TimeUnit

class QuoteLoadingWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters), KoinComponent {

    private val getQuoteUseCase by inject<CoroutineUseCaseWithParams<GetQuoteParams, Quote>>(named(UseCases.GET_QUOTE))
    private val widgetSettingsInteractor by inject<WidgetSettingsInteractor>()
    private val getKeyUseCase by inject<CoroutineUseCase<Int>>(named(UseCases.GET_KEY))
    private val storeQuoteUseCase by inject<CoroutineUseCaseWithParams<Quote, Unit>>(named(UseCases.STORE_QUOTE))
    private val widgetManager by inject<WidgetManager>()

    override suspend fun doWork(): Result = try {
        val widgetParams = widgetSettingsInteractor.getQuoteWidgetParams()
        storeQuoteUseCase.invoke(
            getQuoteUseCase.invoke(
                GetQuoteParams(
                    key = getKeyUseCase.invoke(),
                    lang = widgetParams.quoteLang
                )
            )
        )
        widgetManager.updateQuote()
        Result.success()
    } catch (exception: Exception) {
        Log.e("workerError", exception.toString())
        Result.failure()
    }

    companion object {
        fun createQuoteWidgetWorker(widgetUpdateTimeInHour: Long): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            return PeriodicWorkRequestBuilder<QuoteLoadingWorker>(widgetUpdateTimeInHour, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        }
    }
}