package ru.snakelord.philosofidget.domain.usecase.quote

import kotlin.random.Random.Default.nextInt

class GetKeyUseCase {
    private companion object {
        const val KEY_RANGE_FROM = 0
        const val KEY_RANGE_TO = 999999
    }

    fun invoke(): Int = nextInt(
        from = KEY_RANGE_FROM,
        until = KEY_RANGE_TO
    )
}