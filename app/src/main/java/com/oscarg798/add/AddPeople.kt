package com.oscarg798.add

import com.oscarg798.People
import com.oscarg798.PeopleRepository
import com.oscarg798.UUIDProvider
import kotlinx.coroutines.delay
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
import javax.inject.Inject


class AddPeople @Inject constructor(
    private val uuidProvider: UUIDProvider,
    private val emailPattern: Pattern,
    private val peopleRepository: PeopleRepository
) {

    suspend operator fun invoke(addPeopleParams: AddPeopleParams) {

        if (!emailPattern.matcher(addPeopleParams.email.trim()).matches()) {
            throw InvalidEmailException()
        }

        delay(2_000)

        peopleRepository.addPeople(
            People(
                id = uuidProvider.provide(),
                name = addPeopleParams.name.trim(),
                lastname = addPeopleParams.lastName.trim(),
                email = addPeopleParams.email.trim()
            )
        )
    }

    data class AddPeopleParams(
        val name: String,
        val lastName: String,
        val email: String
    )

    class InvalidEmailException : IllegalArgumentException()
}