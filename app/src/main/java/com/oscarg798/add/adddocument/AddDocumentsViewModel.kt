package com.oscarg798.add.adddocument

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class AddDocumentsViewModel @AssistedInject constructor(
    @Assisted
    private val documentsData: DocumentData
) : ViewModel() {

    private val _state = MutableStateFlow(
        State(
            fieldId = documentsData.fieldId,
            documents = documentsData.documents
        )
    )
    val state: Flow<State> = _state

    private val _events = MutableSharedFlow<Event>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val event: Flow<Event> = _events

    init {
        _state.update {
            it.copy(
                documents = documentsData.documents,
                fieldId = documentsData.fieldId
            )
        }
    }

    fun onAddClicked() {
        viewModelScope.launch {
            _state.update {
                it.copy(documents = it.documents.toMutableList().apply {
                    add(UUID.randomUUID().toString())
                })
            }
        }
    }

    fun onDeleteClicked(document: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(documents = it.documents.toMutableList().apply {
                    remove(document)
                })
            }
        }
    }

    data class State(
        val fieldId: String = "",
        val documents: List<String> = emptyList()
    )

    sealed interface Event

    @AssistedFactory
    interface AddDocumentsViewModelFactory {

        fun create(documentsData: DocumentData): AddDocumentsViewModel
    }
}