package com.oscarg798.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarg798.CorutineDispatcherProvider
import com.oscarg798.People
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getPeople: GetPeople,
    private val refresh: Refresh,
    private val corutineDispatcherProvider: CorutineDispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: Flow<State> = _state

    private val _events = MutableSharedFlow<Event>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val event: Flow<Event> = _events

    init {
        fetchPeople()
    }

    private fun fetchPeople() {
        viewModelScope.launch {

            getPeople().flowOn(corutineDispatcherProvider.io)
                .onStart {
                    _state.update { it.copy(loading = true) }
                }
                .collect { people ->
                    _state.update {
                        it.copy(loading = false, people = people, isRefreshing = false)
                    }
                }
        }
    }

    fun onAddPressed() {
        viewModelScope.launch {
            _events.tryEmit(Event.NavigateToAdd)
        }
    }

    fun refresh() {
        if(_state.value.loading) return

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            refresh.invoke()
        }
    }


    data class State(
        val loading: Boolean = false,
        val people: List<People>? = null,
        val isRefreshing: Boolean = false
    )

    sealed interface Event {

        object NavigateToAdd : Event
    }
}