package com.andriawan24.pawpalace.features.detail.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailPetShopVM @Inject constructor(): ViewModel() {

    private val db = Firebase.firestore

    private val _isGetDetailLoading = MutableStateFlow(false)
    val isGetDetailLoading = _isGetDetailLoading.asStateFlow()

    private val _getDetailError = MutableSharedFlow<String>()
    val getDetailError = _getDetailError.asSharedFlow()

    private val _getDetailSuccess = MutableSharedFlow<PetShopModel>()
    val getDetailSuccess = _getDetailSuccess.asSharedFlow()

    private val _goToMessageRoom = MutableSharedFlow<ChatModel.PetShop>()
    val goToMessageRoom = _goToMessageRoom.asSharedFlow()

    private val _petShopState = MutableStateFlow<PetShopModel?>(null)
    val petShopState = _petShopState.asStateFlow()

    private val _petShopChatState = MutableStateFlow<ChatModel.PetShop?>(null)
    val petShopChatState = _petShopChatState.asStateFlow()

    fun initData(petShopId: String) {
        viewModelScope.launch {
            _isGetDetailLoading.emit(true)
            try {
                val petShopDocument = db.collection("pet_shops")
                    .document(petShopId)
                    .get()
                    .await()

                val petShop = PetShopModel(
                    id = petShopDocument.id,
                    userId = petShopDocument.getString("userId").orEmpty(),
                    description = petShopDocument.getString("description").orEmpty(),
                    location = petShopDocument.getString("location").orEmpty(),
                    dailyPrice = petShopDocument.getLong("dailyPrice")?.toInt() ?: 0,
                    slot = petShopDocument.getLong("slot")?.toInt() ?: 0,
                    name = petShopDocument.getString("name").orEmpty(),
                    rating = petShopDocument.getDouble("rating") ?: 0.0,
                    rated = petShopDocument.getLong("rated")?.toInt() ?: 0
                )

                val petShopChat = ChatModel.PetShop(
                    id = petShopDocument.id,
                    userId = petShopDocument.getString("userId").orEmpty(),
                    name = petShopDocument.getString("name").orEmpty()
                )

                _petShopChatState.emit(petShopChat)
                _petShopState.emit(petShop)
                _getDetailSuccess.emit(petShop)
                _isGetDetailLoading.emit(false)
            } catch (e: Exception) {
                Timber.e(e)
                _isGetDetailLoading.emit(false)
                _getDetailError.emit(e.localizedMessage.orEmpty())
            }
        }
    }

    fun onMessageClicked() {
        viewModelScope.launch {
            _petShopChatState.value?.let { petShopModel ->
                _goToMessageRoom.emit(petShopModel)
            }
        }
    }
}