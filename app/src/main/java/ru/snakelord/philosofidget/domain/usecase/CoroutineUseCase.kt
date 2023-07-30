package ru.snakelord.philosofidget.domain.usecase

interface CoroutineUseCase<Input, Output> {
    suspend fun invoke(params: Input): Output
}