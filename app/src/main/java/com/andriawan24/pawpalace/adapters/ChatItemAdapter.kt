package com.andriawan24.pawpalace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.data.models.ChatModel
import com.andriawan24.pawpalace.databinding.ViewChatDateHeaderBinding
import com.andriawan24.pawpalace.databinding.ViewChatReceiverItemBinding
import com.andriawan24.pawpalace.databinding.ViewChatSenderItemBinding
import com.andriawan24.pawpalace.utils.RecyclerDiffUtil
import java.text.SimpleDateFormat
import java.util.Locale

class ChatItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chats = emptyList<Any>()

    class ChatItemDateHeader(
        private val binding: ViewChatDateHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dateString: String) {
            binding.textViewDate.text = dateString
        }
    }

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
        } else if (viewType == VIEW_TYPE_RECEIVER) {
            val binding = ViewChatReceiverItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ChatItemReceiverViewHolder(binding)
        } else {
            val binding = ViewChatDateHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ChatItemDateHeader(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chats[position]

        if (chat is ChatModel) {
            if (chat.fromSender) {
                (holder as ChatItemSenderViewHolder).bind(chat)
            } else {
                (holder as ChatItemReceiverViewHolder).bind(chat)
            }
        } else {
            (holder as ChatItemDateHeader).bind(chat.toString())
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = chats[position]
        return if (chat is ChatModel) {
            if (chat.fromSender) {
                VIEW_TYPE_SENDER
            } else {
                VIEW_TYPE_RECEIVER
            }
        } else {
            VIEW_TYPE_HEADER
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun setData(newItems: List<Any>) {
        val diffUtil = RecyclerDiffUtil(chats, newItems)
        val result = DiffUtil.calculateDiff(diffUtil)
        chats = newItems
        result.dispatchUpdatesTo(this)
    }

    companion object {
        const val VIEW_TYPE_SENDER = 1
        const val VIEW_TYPE_RECEIVER = 2
        const val VIEW_TYPE_HEADER = 3
    }
}