package com.oscarg798.add.adddocument

import com.oscarg798.add.AddFlowScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AddFlowScope
class DocumentRepository @Inject constructor() {

    private val documents: MutableStateFlow<Map<String, Set<String>>> =
        MutableStateFlow(mutableMapOf())

    fun getDocuments(fieldId: String): Flow<Set<String>> = documents.map {
        it[fieldId] ?: setOf()
    }

    fun addDocument(fieldId: String, document: String) {
        val newDocuments = documents.value.toMutableMap().apply {
            if (containsKey(fieldId)) {
                put(fieldId, get(fieldId)!!.toMutableSet().apply {
                    add(document)
                })
            } else {
                put(fieldId, setOf(document))
            }
        }

        documents.value = newDocuments
    }

    fun deleteDocument(fieldId: String, document: String) {
        val newDocuments = documents.value.toMutableMap().apply {
            val documents = get(fieldId)?.toMutableSet() ?: return
            documents.remove(document)
            this[fieldId] = documents
        }
        documents.value = newDocuments

    }
}