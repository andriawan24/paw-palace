package com.andriawan24.pawpalace.features.chat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatListVM @Inject constructor(private val datastore: PawPalaceDatastore): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _isGetChatLoading = MutableStateFlow(false)
    val isGetChatLoading = _isGetChatLoading.asStateFlow()

    private val _chats = MutableStateFlow<Map<ChatModel.PetShop, List<ChatModel>>>(emptyMap())
    val chats = _chats.asStateFlow()

    private val _chatsPetShop = MutableStateFlow<Map<UserModel, List<ChatModel>>>(emptyMap())
    val chatsPetShop = _chatsPetShop.asStateFlow()

    private val _getChatError = MutableSharedFlow<String>()
    val getChatError = _getChatError.asSharedFlow()

    private val _setPetShop = MutableSharedFlow<None>()
    val setPetShop = _setPetShop.asSharedFlow()

    private val _setPetOwner = MutableSharedFlow<None>()
    val setPetOwner = _setPetOwner.asSharedFlow()

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    fun initData() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            val petShop = datastore.getCurrentPetShop().first()
            if (currentUser != null) {
                if (petShop != null) {
                    _setPetShop.emit(None)
                    getPetShopMessages(petShop)
                } else {
                    _setPetOwner.emit(None)
                    getMessages(currentUser.uid)
                }
            } else {
                auth.signOut()
                datastore.setCurrentUser(null)
                datastore.setCurrentPetShop(null)
                _navigateToOnboarding.emit(None)
            }
        }
    }

    private fun getPetShopMessages(petShop: PetShopModel) {
        viewModelScope.launch {
            _isGetChatLoading.emit(true)
            db.collection(ChatModel.REFERENCE_NAME)
                .whereEqualTo("petShopId", petShop.id)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .catch {
                    Timber.e(it)
                    _isGetChatLoading.emit(false)
                    _getChatError.emit(it.localizedMessage.orEmpty())
                }
                .map { snapshots ->
                    snapshots.documents.map(ChatModel::from)
                }
                .collectLatest {
                    val chatGrouped = it.groupBy { chat -> chat.sender }
                    _isGetChatLoading.emit(false)
                    _chatsPetShop.emit(chatGrouped)
                }
        }
    }

    private fun getMessages(senderId: String) {
        viewModelScope.launch {
            _isGetChatLoading.emit(true)
            db.collection(ChatModel.REFERENCE_NAME)
                .whereEqualTo("senderId", senderId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .catch {
                    Timber.e(it)
                    _isGetChatLoading.emit(false)
                    _getChatError.emit(it.localizedMessage.orEmpty())
                }
                .map { snapshots ->
                    snapshots.documents.map(ChatModel::from)
                }
                .collectLatest {
                    val chatGrouped = it.groupBy { chat -> chat.petShop }
                    _isGetChatLoading.emit(false)
                    _chats.emit(chatGrouped)
                }
        }
    }
}