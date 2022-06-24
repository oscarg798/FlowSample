package com.oscarg798

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

interface CorutineDispatcherProvider {

    val io: CoroutineDispatcher
}

@InstallIn(SingletonComponent::class)
@Module
object CoroutineModule {

    @Singleton
    @Provides
    fun provideCoroutinesDispatcher() = object : CorutineDispatcherProvider {
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO

    }
}