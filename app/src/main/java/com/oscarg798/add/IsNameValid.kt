package com.oscarg798.add

import javax.inject.Inject

class IsNameValid  @Inject constructor(){

     operator fun invoke(name: String, lastname: String): Boolean =
        name.length > 3 && lastname.length > 3
}