package com.oscarg798.list

import com.oscarg798.PeopleRepository
import javax.inject.Inject

class GetPeople @Inject constructor(
    private val peopleRepository: PeopleRepository
) {

    operator fun invoke() = peopleRepository.getPeople()
}