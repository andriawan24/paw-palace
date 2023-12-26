package com.andriawan24.pawpalace.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.andriawan24.pawpalace.data.models.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PawPalaceDatastore.DATASTORE_NAME)

class PawPalaceDatastore(context: Context) {

    private val datastore = context.dataStore
    private val currentUserPreferences = stringPreferencesKey(USER_PREFERENCE)

    fun getCurrentUser(): Flow<UserModel> = datastore.data
        .map {
            val userString = it[currentUserPreferences] ?: ""
            val user = Gson().fromJson(userString, UserModel::class.java)
            user
        }

    suspend fun setCurrentUser(userModel: UserModel?) = datastore.edit {
        it[currentUserPreferences] = if (userModel != null) Gson().toJson(userModel) else ""
    }

    companion object {
        const val DATASTORE_NAME = "PawPalaceDatastore"
        const val USER_PREFERENCE = "USER_PREFERENCE"
    }
}