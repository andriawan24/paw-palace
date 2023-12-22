package com.andriawan24.pawpalace.data.repositories

import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import kotlinx.coroutines.flow.Flow

class OnboardingRepository(private val dataStore: PawPalaceDatastore) {

    fun getFirstTime(): Flow<Boolean> {
        return dataStore.isFirstTimeFlow
    }

    suspend fun setFirstTime(firstTime: Boolean) {
        dataStore.setFirstTime(firstTime)
    }
}