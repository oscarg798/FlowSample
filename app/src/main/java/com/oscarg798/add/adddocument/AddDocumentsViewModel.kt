package com.oscarg798.add.adddocument

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarg798.add.AddFlowScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.UUID
import javax.inject.Inject


class AddDocumentsViewModel @AssistedInject constructor(
    @Assisted
    private val fieldId: String,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: Flow<State> = _state

    private val _events = MutableSharedFlow<Event>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val event: Flow<Event> = _events


    init {
        viewModelScope.launch {
            documentRepository.getDocuments(fieldId)
                .flowOn(Dispatchers.IO)
                .collect { documents ->
                    _state.update {
                        it.copy(documents = documents.toList())
                    }
                }
        }
    }

    fun onAddClicked() {
        viewModelScope.launch {
            documentRepository.addDocument(fieldId, UUID.randomUUID().toString())
        }
    }

    fun onDeleteClicked(document: String) {
        viewModelScope.launch {
            documentRepository.deleteDocument(fieldId, document)
        }
    }

    data class State(
        val documents: List<String> = emptyList()
    )

    sealed interface Event

    @AddFlowScope
    @AssistedFactory
    interface AddDocumentsViewModelFactory {

        fun create(fieldId: String): AddDocumentsViewModel
    }
}