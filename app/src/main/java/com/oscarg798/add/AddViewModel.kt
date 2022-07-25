package com.oscarg798.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarg798.add.adddocument.DocumentData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddViewModel @Inject constructor(
    private val addPeople: AddPeople,
    private val isNameValid: IsNameValid
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: Flow<State> = _state

    private val mutex = Mutex()

    private val _events = MutableSharedFlow<Event>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )


    val event: Flow<Event> = _events

    fun onAddClicked(fieldId: String) {
        if(fieldId == field1Id){
            _events.tryEmit(Event.OpenAdd(fieldId, _state.value.documents1))
        }else{
            _events.tryEmit(Event.OpenAdd(fieldId, _state.value.documents2))
        }

    }

    fun onDeleteClicked(fieldId: String, document: String) {
        viewModelScope.launch {
            _state.update {
                if (fieldId == field1Id) {
                    it.copy(
                        documents1 = it.documents1.toMutableList().apply {
                            remove(document)
                        }
                    )
                } else {
                    it.copy(
                        documents2 = it.documents2.toMutableList().apply {
                            remove(document)
                        }
                    )
                }
            }
        }
    }

    fun addPeople() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }

            withContext(Dispatchers.IO) {
                addPeople(
                    AddPeople.AddPeopleParams(
                        name = _state.value.name,
                        lastName = _state.value.lastName,
                        email = _state.value.email
                    )
                )
            }

            _events.tryEmit(Event.ReturnToList)
        }
    }

    fun simulateList(value: Int) {
        viewModelScope.launch {
            delay(1_500)
            _state.update2 {
                if (value == 1) {
                    it.copy(lis = listOf("Oscar", "Pablo", "Jacinto"))
                } else {
                    it.copy(lis = (0..Random.nextInt(10)).map {
                        UUID.randomUUID().toString()
                    })
                }

            }
        }
    }

    fun onNameChange(name: String) {
        _state.update2 { it.copy(name = name) }
    }

    fun onLastNameChange(lastName: String) {
        _state.update2 { it.copy(lastName = lastName) }
    }

    fun onEmailChange(email: String) {
        _state.update2 { it.copy(email = email) }
    }

    fun onPageScrolled(position: Int) {
        _events.tryEmit(Event.None)
        if (position == 1 && !isNameValid(_state.value.name, _state.value.lastName)) {
            _state.update2 { it.copy(errors = State.DataError.NameError) }
            _events.tryEmit(Event.SwipeBack)

        } else if (position == 1) {
            _state.update2 { it.copy(errors = null) }
        }
    }

    fun onDocumentsUpdated(documentData: DocumentData){
        viewModelScope.launch {

               if (documentData.fieldId == field1Id) {
                   _state.update {
                       it.copy(
                           documents1 = documentData.documents
                       )
                   }
               } else if(documentData.fieldId == field2Id){
                   _state.update {
                       it.copy(
                           documents2 = documentData.documents
                       )
                   }
               }

        }
    }

    private fun MutableStateFlow<State>.update2(update: (State) -> State) {
        viewModelScope.launch {
            mutex.withLock {
                _state.update(update)
            }
        }
    }

    data class State(
        val name: String = "",
        val lastName: String = "",
        val documents1: List<String> = emptyList(),
        val documents2: List<String> = emptyList(),
        val email: String = "",
        val errors: DataError? = null,
        val loading: Boolean = false,
        val lis: List<String> = emptyList(),
        val page: Int = 0
    ) {
        sealed interface DataError {
            object NameError : DataError
            object LastNameError : DataError
        }
    }


    sealed interface Event {
        object None : Event
        object ReturnToList : Event
        object SwipeBack : Event
        data class OpenAdd(
            val fieldId: String,
            val documents: List<String>
        ) : Event
    }

    companion object {
        val field1Id = UUID.randomUUID().toString()
        val field2Id = UUID.randomUUID().toString()
    }

}