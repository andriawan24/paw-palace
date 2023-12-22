package com.andriawan24.pawpalace.di

import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.repositories.OnboardingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @ViewModelScoped
    @Provides
    fun providesOnboardingRepository(dataStore: PawPalaceDatastore): OnboardingRepository {
        return OnboardingRepository(dataStore = dataStore)
    }
}