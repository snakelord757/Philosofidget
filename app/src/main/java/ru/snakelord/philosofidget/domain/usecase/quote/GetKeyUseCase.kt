package ru.snakelord.philosofidget.domain.usecase.quote

import ru.snakelord.philosofidget.domain.usecase.CoroutineUseCase
import kotlin.random.Random.Default.nextInt

class GetKeyUseCase : CoroutineUseCase<Int> {
    override suspend fun invoke(): Int = nextInt(
        from = KEY_RANGE_FROM,
        until = KEY_RANGE_TO
    )

    private companion object {
        const val KEY_RANGE_FROM = 0
        const val KEY_RANGE_TO = 999999
    }
}