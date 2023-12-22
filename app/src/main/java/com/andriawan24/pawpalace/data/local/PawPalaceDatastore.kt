package com.andriawan24.pawpalace.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PawPalaceDatastore.DATASTORE_NAME)

class PawPalaceDatastore(context: Context) {

    private val datastore = context.dataStore

    private val firstTimePreferenceKey = booleanPreferencesKey(IS_FIRST_TIME_KEY)

    val isFirstTimeFlow: Flow<Boolean> = datastore.data
        .map { preferences ->
            preferences[firstTimePreferenceKey] ?: true
        }

    suspend fun setFirstTime(isFirstTime: Boolean) {
        datastore.edit { preference ->
            preference[firstTimePreferenceKey] = isFirstTime
        }
    }

    companion object {
        const val DATASTORE_NAME = "PawPalaceDatastore"
        private const val IS_FIRST_TIME_KEY = "IS_FIRST_TIME_KEY"
    }
}