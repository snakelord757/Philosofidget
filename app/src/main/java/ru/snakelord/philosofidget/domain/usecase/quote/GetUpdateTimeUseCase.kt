package ru.snakelord.philosofidget.domain.usecase.quote

import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository
import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase

class GetUpdateTimeUseCase(private val widgetSettingsRepository: WidgetSettingsRepository) : CoroutineUseCase<Long> {
    override suspend fun invoke(): Long = widgetSettingsRepository.getWidgetUpdateTime()
}