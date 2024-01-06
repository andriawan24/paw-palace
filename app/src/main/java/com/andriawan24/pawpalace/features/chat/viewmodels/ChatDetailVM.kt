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
import com.google.firebase.firestore.Filter
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
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatDetailVM @Inject constructor(private val datastore: PawPalaceDatastore) : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _getChatLoading = MutableStateFlow(false)
    val getChatLoading = _getChatLoading.asStateFlow()

    private val _getChatError = MutableSharedFlow<String>()
    val getChatError = _getChatError.asSharedFlow()

    private val _chats = MutableStateFlow<Map<String, List<ChatModel>>>(emptyMap())
    val chats = _chats.asStateFlow()

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    private var currentUser: UserModel? = null

    fun initData(petShopId: String, userId: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            val petShop = datastore.getCurrentPetShop().first()
            if (currentUser != null) {
                this@ChatDetailVM.currentUser = datastore.getCurrentUser().first()
                if (petShop != null) {
                    getMessagesPetShops(userId, petShopId)
                } else {
                    getMessages(currentUser.uid, petShopId)
                }
                updateAllMessageToRead(currentUser.uid, petShopId)
            } else {
                auth.signOut()
                datastore.setCurrentUser(null)
                datastore.setCurrentPetShop(null)
                _navigateToOnboarding.emit(None)
            }
        }
    }

    private fun updateAllMessageToRead(senderId: String, petShopId: String) {
        viewModelScope.launch {
            val chat = db.collection("chats")
                .where(
                    Filter.and(
                        Filter.equalTo("senderId", senderId),
                        Filter.equalTo("petShopId", petShopId)
                    )
                )
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .firstOrNull()

            if (chat != null && chat.getBoolean("read") == false) {
                db.collection("chats")
                    .document(chat.id)
                    .update("read", true)
                    .await()
            }
        }
    }

    private fun getMessagesPetShops(senderId: String, petShopId: String) {
        viewModelScope.launch {
            _getChatLoading.emit(true)
            db.collection(ChatModel.REFERENCE_NAME)
                .where(
                    Filter.and(
                        Filter.equalTo("senderId", senderId),
                        Filter.equalTo("petShopId", petShopId)
                    )
                )
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .catch {
                    Timber.e(it)
                    _getChatLoading.emit(false)
                    _getChatError.emit(it.localizedMessage.orEmpty())
                }
                .map { snapshots ->
                    snapshots.documents.map { document ->
                        ChatModel.from(document)
                    }
                }
                .collectLatest {
                    val chatByDate = it.groupBy { chat ->
                        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        val date = simpleDateFormat.format(chat.createdAt)
                        date
                    }
                    Timber.d("Chat by date $chatByDate")
                    _getChatLoading.emit(false)
                    _chats.emit(chatByDate)
                }
        }
    }

    private fun getMessages(senderId: String, petShopId: String) {
        viewModelScope.launch {
            _getChatLoading.emit(true)
            db.collection(ChatModel.REFERENCE_NAME)
                .where(
                    Filter.and(
                        Filter.equalTo("senderId", senderId),
                        Filter.equalTo("petShopId", petShopId)
                    )
                )
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .catch {
                    Timber.e(it)
                    _getChatLoading.emit(false)
                    _getChatError.emit(it.localizedMessage.orEmpty())
                }
                .map { snapshots ->
                    snapshots.documents.map { document ->
                        ChatModel.from(document)
                    }
                }
                .collectLatest {
                    val chatByDate = it.groupBy { chat ->
                        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        val date = simpleDateFormat.format(chat.createdAt)
                        date
                    }
                    Timber.d("Chat by date $chatByDate")
                    _getChatLoading.emit(false)
                    _chats.emit(chatByDate)
                }
        }
    }

    fun sendMessage(text: String, petShop: ChatModel.PetShop, userId: String = "") {
        viewModelScope.launch {
            currentUser?.let {
                val chatDoc = db.collection(ChatModel.REFERENCE_NAME)
                    .document()

                if (userId.isNotEmpty()) { // From Pet Shop
                    val userDocument = db.collection(UserModel.REFERENCE_NAME)
                        .document(userId)
                        .get()
                        .await()

                    val user = UserModel.from(userDocument)

                    val chat = ChatModel(
                        id = chatDoc.id,
                        sender = user,
                        petShop = petShop,
                        senderId = user.id,
                        petShopId = petShop.id,
                        text = text,
                        fromSender = "petShop"
                    )

                    try {
                        chatDoc.set(chat).await()
                    } catch (e: Exception) {
                        Timber.e(e)
                        _getChatError.emit(e.localizedMessage.orEmpty())
                    }
                } else {
                    val chat = ChatModel(
                        id = chatDoc.id,
                        sender = it,
                        petShop = petShop,
                        senderId = userId.ifEmpty { it.id },
                        petShopId = petShop.id,
                        text = text,
                        fromSender = "petOwner"
                    )

                    try {
                        chatDoc.set(chat).await()
                    } catch (e: Exception) {
                        Timber.e(e)
                        _getChatError.emit(e.localizedMessage.orEmpty())
                    }
                }
            }
        }
    }
}