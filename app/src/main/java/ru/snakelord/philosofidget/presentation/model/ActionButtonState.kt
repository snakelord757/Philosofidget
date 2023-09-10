package ru.snakelord.philosofidget.presentation.model

data class ActionButtonState(
    val title: String,
    val onClickAction: () -> Unit
)