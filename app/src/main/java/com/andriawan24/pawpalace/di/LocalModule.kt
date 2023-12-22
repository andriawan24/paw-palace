package com.andriawan24.pawpalace.di

import android.content.Context
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun providesPawPalaceDatastore(@ApplicationContext context: Context): PawPalaceDatastore {
        return PawPalaceDatastore(context)
    }
}