package com.andriawan24.pawpalace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.databinding.ViewChatReceiverItemBinding
import com.andriawan24.pawpalace.databinding.ViewChatSenderItemBinding
import com.andriawan24.pawpalace.utils.RecyclerDiffUtil
import java.text.SimpleDateFormat
import java.util.Locale

class ChatItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chats = emptyList<ChatModel>()

    class ChatItemSenderViewHolder(
        private val binding: ViewChatSenderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ChatModel) {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

            binding.textViewMessage.text = chat.text
            binding.textViewTime.text = formatter.format(chat.createdAt)
        }
    }

    class ChatItemReceiverViewHolder(
        private val binding: ViewChatReceiverItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ChatModel) {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

            binding.textViewMessage.text = chat.text
            binding.textViewTime.text = formatter.format(chat.createdAt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val binding = ViewChatSenderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ChatItemSenderViewHolder(binding)
        } else {
            val binding = ViewChatReceiverItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ChatItemReceiverViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chats[position]

        if (chat.fromSender) {
            (holder as ChatItemSenderViewHolder).bind(chat)
        } else {
            (holder as ChatItemReceiverViewHolder).bind(chat)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chats[position].fromSender) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun setData(newItems: List<ChatModel>) {
        val diffUtil = RecyclerDiffUtil(chats, newItems)
        val result = DiffUtil.calculateDiff(diffUtil)
        chats = newItems
        result.dispatchUpdatesTo(this)
    }

    companion object {
        const val VIEW_TYPE_SENDER = 1
        const val VIEW_TYPE_RECEIVER = 2
    }
}