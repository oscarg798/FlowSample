package com.oscarg798

import java.util.UUID
import javax.inject.Inject

class UUIDProvider @Inject constructor() {

    fun provide(): UUID = UUID.randomUUID()
}