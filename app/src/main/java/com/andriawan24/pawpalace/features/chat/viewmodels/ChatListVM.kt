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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatListVM @Inject constructor(private val datastore: PawPalaceDatastore): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _isGetChatLoading = MutableStateFlow(false)
    val isGetChatLoading = _isGetChatLoading.asStateFlow()

    private val _chats = MutableStateFlow<Map<PetShopModel, List<ChatModel>>>(emptyMap())
    val chats = _chats.asStateFlow()

    private val _getChatError = MutableSharedFlow<String>()
    val getChatError = _getChatError.asSharedFlow()

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    fun initData() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                getMessages(currentUser.uid)
            } else {
                auth.signOut()
                datastore.setCurrentUser(null)
                datastore.setCurrentPetShop(null)
                _navigateToOnboarding.emit(None)
            }
        }
    }

    private fun getMessages(senderId: String) {
        viewModelScope.launch {
            _isGetChatLoading.emit(true)
            db.collection("chats")
                .whereEqualTo("senderId", senderId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .catch {
                    Timber.e(it)
                    _isGetChatLoading.emit(false)
                    _getChatError.emit(it.localizedMessage.orEmpty())
                }
                .map { snapshots ->
                    snapshots.documents.map { document ->
                        val sender = document.get("sender") as? HashMap<*, *>
                        val receiver = document.get("petShop") as? HashMap<*, *>
                        ChatModel(
                            id = document.id,
                            sender = UserModel(
                                id = sender?.get("id").toString(),
                                name = sender?.get("name").toString(),
                                email = sender?.get("email").toString(),
                                phoneNumber = sender?.get("phoneNumber").toString(),
                                location = sender?.get("location").toString(),
                            ),
                            petShop = PetShopModel(
                                id = receiver?.get("id").toString(),
                                name = receiver?.get("name").toString(),
                                description = receiver?.get("description").toString(),
                                userId = receiver?.get("userId").toString(),
                                location = receiver?.get("location").toString(),
                                dailyPrice = receiver?.get("dailyPrice").toString().toIntOrNull()
                                    ?: 0,
                                slot = receiver?.get("slot").toString().toIntOrNull() ?: 0,
                            ),
                            text = document.getString("text").orEmpty(),
                            createdAt = document.getDate("createdAt") ?: Date(),
                            read = document.getBoolean("read") ?: false,
                            senderId = document.getString("senderId").orEmpty(),
                            petShopId = document.getString("petShopId").orEmpty()
                        )
                    }
                }
                .collectLatest {
                    Timber.e("Chat List: $it")
                    val chatGrouped = it.groupBy { chat -> chat.petShop }
                    _isGetChatLoading.emit(false)
                    _chats.emit(chatGrouped)
                }
        }
    }
}