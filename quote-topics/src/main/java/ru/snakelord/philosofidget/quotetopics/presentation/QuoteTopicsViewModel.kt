package ru.snakelord.philosofidget.quotetopics.presentation

import androidx.lifecycle.ViewModel
import ru.snakelord.philosofidget.quotetopics.domain.QuoteTopicsInteractor

class QuoteTopicsViewModel(private val interactor: QuoteTopicsInteractor) : ViewModel() {
    val topics = interactor.topics
    val selectedTopic = interactor.selectedTopic
    fun selectTopic(id: String) = interactor.selectTopic(id)
}
