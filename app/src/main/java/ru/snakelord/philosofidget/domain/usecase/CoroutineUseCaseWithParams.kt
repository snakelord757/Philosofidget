package ru.snakelord.philosofidget.domain.usecase

interface CoroutineUseCaseWithParams<Input, Output> {
    suspend fun invoke(params: Input): Output
}