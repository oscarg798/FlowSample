package com.oscarg798.di

import android.util.Patterns
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.regex.Pattern

@InstallIn(SingletonComponent::class)
@Module
object PatternModule  {

    @Reusable
    @Provides
    fun provideEmailPatter(): Pattern = Patterns.EMAIL_ADDRESS

}