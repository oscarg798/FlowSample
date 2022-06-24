package com.oscarg798.list

import com.oscarg798.PeopleRepository
import javax.inject.Inject

class Refresh @Inject constructor(
    private val peopleRepository: PeopleRepository
) {


    suspend operator fun invoke(){
        peopleRepository.refresh()
    }
}