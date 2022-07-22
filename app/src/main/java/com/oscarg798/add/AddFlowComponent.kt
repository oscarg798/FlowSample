package com.oscarg798.add

import com.oscarg798.add.adddocument.AddDocumentsViewModel
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope

@AddFlowScope
@DefineComponent(parent = ActivityRetainedComponent::class)
interface AddFlowComponent {

    @DefineComponent.Builder
    interface Builder {
        fun build(): AddFlowComponent
    }
}

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Scope
annotation class AddFlowScope


interface AddFlowComponentProvider {

    fun provide(): AddFlowComponent
}

@InstallIn(AddFlowComponent::class)
@EntryPoint
interface AddDocumentDialogComponentEntryPoint {

    fun getAddDocumentsViewModelFactory(): AddDocumentsViewModel.AddDocumentsViewModelFactory
}

@InstallIn(AddFlowComponent::class)
@EntryPoint
interface AddViewModelEntryPoint {

    fun getAddViewModel(): AddViewModel
}