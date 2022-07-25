package com.oscarg798

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeopleRepository @Inject constructor(
    private val uuidProvider: UUIDProvider
) {

    private val peopleStream = MutableStateFlow(listOf<People>())

    fun getPeople(): Flow<List<People>> = peopleStream.onStart {
        delay(3_000)
    }

    fun addPeople(people: People) {
        peopleStream.value = peopleStream.value.toMutableList().apply {
            add(people)
        }
    }

    suspend fun refresh() {
        delay(1_000)
        peopleStream.value = peopleStream.value.toMutableList().apply {
            add(People(
                id = uuidProvider.provide(),
                name = "Jacob",
                lastname = "Lindo",
                email = "lindo@gmail.com"
            ))
        }

    }
}