package ru.snakelord.philosofidget.presentation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.domain.interactor.WidgetSettingsInteractor
import ru.snakelord.philosofidget.domain.model.GetQuoteParams
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.usecase.quote.GetKeyUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetQuoteUseCase
import ru.snakelord.philosofidget.presentation.mapper.QuoteWidgetStateMapper
import ru.snakelord.philosofidget.presentation.model.QuoteWidgetState

class QuoteViewModel(
    private val getQuoteUseCase: GetQuoteUseCase,
    private val quoteWidgetStateMapper: QuoteWidgetStateMapper,
    private val workingDispatcher: CoroutineDispatcher,
    private val getKeyUseCase: GetKeyUseCase,
    private val widgetSettingsInteractor: WidgetSettingsInteractor
) : ViewModel() {

    private val quoteStateFlow = MutableStateFlow<QuoteWidgetState>(QuoteWidgetState.Loading)
    val quoteState: StateFlow<QuoteWidgetState>
        get() = quoteStateFlow.asStateFlow()

    fun loadQuote() {
        viewModelScope.launch(workingDispatcher) {
            val quoteFlow = flowOf(getQuoteUseCase.invoke(GetQuoteParams(getKeyUseCase.invoke(), Lang.RU)))
            val quoteWidgetParamsFlow = flowOf(widgetSettingsInteractor.getQuoteWidgetParams())
            quoteFlow.zip(quoteWidgetParamsFlow) { quote, quoteWidgetParams -> quoteWidgetStateMapper.map(quote, quoteWidgetParams) }
                .collect { quoteStateFlow.emit(it) }
        }
    }
}