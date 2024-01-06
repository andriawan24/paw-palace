package com.andriawan24.pawpalace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.databinding.ViewChatListItemBinding
import com.andriawan24.pawpalace.utils.RecyclerDiffUtil
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListPetShopAdapter(
    private val listener: OnClickListener
) : RecyclerView.Adapter<ChatListPetShopAdapter.ChatListItemViewHolder>() {

    private var chats = emptyList<Pair<UserModel, ChatModel>>()

    class ChatListItemViewHolder(
        private val binding: ViewChatListItemBinding,
        private val listener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Pair<UserModel, ChatModel>) {
            binding.textViewName.text = chat.first.name
            binding.textViewLastChat.text = chat.second.text
            if (!chat.second.read && chat.second.fromSender != "petOwner") {
                binding.textViewLastChat.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorPrimary
                    )
                )
            } else {
                binding.textViewLastChat.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.textPrimary
                    )
                )
            }
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.textViewDate.text = dateFormatter.format(chat.second.createdAt)
            binding.linearLayoutContainer.setOnClickListener { listener.onChatPetShopClicked(chat.second) }
        }
    }

    override fun getItemCount(): Int = chats.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListItemViewHolder {
        val binding = ViewChatListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ChatListItemViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    fun setItem(newItems: List<Pair<UserModel, ChatModel>>) {
        val diffUtil = RecyclerDiffUtil(chats, newItems)
        val result = DiffUtil.calculateDiff(diffUtil)
        chats = newItems
        result.dispatchUpdatesTo(this)
    }

    interface OnClickListener {
        fun onChatPetShopClicked(chat: ChatModel)
    }
}