package ru.snakelord.philosofidget.domain.usecase.widget_settings

import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase

class RemoveQuoteParamsUseCase(private val widgetSettingsInteractor: WidgetSettingsInteractor) : CoroutineUseCase<Unit> {
    override suspend fun invoke() {
        widgetSettingsInteractor.clearQuoteWidgetParams()
    }
}