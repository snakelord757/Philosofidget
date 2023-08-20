package ru.snakelord.philosofidget.domain.usecase

interface CoroutineUseCase<Output> {
    suspend fun invoke(): Output
}