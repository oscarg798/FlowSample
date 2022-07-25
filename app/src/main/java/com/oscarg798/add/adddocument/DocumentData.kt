package com.oscarg798.add.adddocument

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DocumentData(
    val documents: List<String>,
    val fieldId: String
): Parcelable