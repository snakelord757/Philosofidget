package ru.snakelord.philosofidget.presentation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.domain.ext.Mapper
import ru.snakelord.philosofidget.domain.model.GetQuoteParams
import ru.snakelord.philosofidget.domain.model.Lang
import ru.snakelord.philosofidget.domain.model.Quote
import ru.snakelord.philosofidget.domain.usecase.quote.GetKeyUseCase
import ru.snakelord.philosofidget.domain.usecase.quote.GetQuoteUseCase
import ru.snakelord.philosofidget.presentation.model.QuoteWidgetState

class QuoteViewModel(
    private val getQuoteUseCase: GetQuoteUseCase,
    private val quoteWidgetStateMapper: Mapper<Quote, QuoteWidgetState>,
    private val workingDispatcher: CoroutineDispatcher,
    private val getKeyUseCase: GetKeyUseCase
) : ViewModel() {

    private val quoteStateFlow = MutableStateFlow<QuoteWidgetState>(QuoteWidgetState.Loading)
    val quoteState: StateFlow<QuoteWidgetState>
        get() = quoteStateFlow.asStateFlow()

    fun loadQuote() {
        viewModelScope.launch(workingDispatcher) {
            runCatching { getQuoteUseCase.invoke(GetQuoteParams(getKeyUseCase.invoke(), Lang.RU)) }
                .onSuccess { quoteStateFlow.emit(quoteWidgetStateMapper.invoke(it)) }
                .onFailure { quoteStateFlow.emit(QuoteWidgetState.Error) }
        }
    }
}