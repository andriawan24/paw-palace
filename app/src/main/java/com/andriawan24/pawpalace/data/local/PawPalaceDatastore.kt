package com.andriawan24.pawpalace.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PawPalaceDatastore.DATASTORE_NAME)

class PawPalaceDatastore(context: Context) {

    private val datastore = context.dataStore
    private val currentUserPreferences = stringPreferencesKey(USER_PREFERENCE)
    private val currentPetShopPreferences = stringPreferencesKey(PET_SHOP_PREFERENCE)

    fun getCurrentUser(): Flow<UserModel?> = datastore.data
        .map {
            val userString = it[currentUserPreferences] ?: return@map null
            val user = Gson().fromJson(userString, UserModel::class.java)
            user
        }

    fun getCurrentPetShop(): Flow<PetShopModel?> = datastore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            val petShopString = it[currentPetShopPreferences] ?: return@map null
            val petShop = Gson().fromJson(petShopString, PetShopModel::class.java)
            petShop
        }

    suspend fun setCurrentUser(user: UserModel?) = datastore.edit {
        it[currentUserPreferences] = if (user != null) Gson().toJson(user) else ""
    }

    suspend fun setCurrentPetShop(petShop: PetShopModel?) = datastore.edit {
        it[currentPetShopPreferences] = if (petShop != null) Gson().toJson(petShop) else ""
    }

    companion object {
        const val DATASTORE_NAME = "PawPalaceDatastore"
        const val USER_PREFERENCE = "USER_PREFERENCE"
        const val PET_SHOP_PREFERENCE = "PET_SHOP_PREFERENCE"
    }
}