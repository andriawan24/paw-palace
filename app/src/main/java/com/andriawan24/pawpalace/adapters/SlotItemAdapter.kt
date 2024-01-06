package com.andriawan24.pawpalace.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.databinding.ViewSlotEmptyItemBinding
import com.andriawan24.pawpalace.databinding.ViewSlotItemBinding
import com.andriawan24.pawpalace.utils.RecyclerDiffUtil
import java.text.SimpleDateFormat
import java.util.Locale

class SlotItemAdapter(private val listener: OnClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var slots: List<BookingModel?> = emptyList()

    class SlotItemViewHolder(private val binding: ViewSlotItemBinding, private val listener: OnClickListener): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(slot: BookingModel) {
            binding.textViewName.text = slot.petOwner.name
            binding.textViewPhone.text = slot.petOwner.phoneNumber

            val simpleDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.textViewDate.text = "${simpleDateFormatter.format(slot.startDate)} - ${simpleDateFormatter.format(slot.endDate)}"

            binding.buttonChat.setOnClickListener {
                listener.onChatClicked(slot.petOwner, slot.petShop)
            }

            binding.buttonDetail.setOnClickListener {
                listener.onDetailOrderClickedClicked(slot)
            }
        }
    }

    class SlotItemEmptyViewHolder(binding: ViewSlotEmptyItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return slots.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_BOOKING) {
            val binding = ViewSlotItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SlotItemViewHolder(binding, listener)
        } else {
            val binding = ViewSlotEmptyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SlotItemEmptyViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val slot = slots.getOrNull(position)
        if (slot != null) (holder as SlotItemViewHolder).bind(slot)
    }

    override fun getItemViewType(position: Int): Int {
        return if (slots.getOrNull(position) != null) VIEW_TYPE_BOOKING else VIEW_TYPE_EMPTY
    }

    fun setData(newData: List<BookingModel?>) {
        val diffUtil = RecyclerDiffUtil(slots, newData)
        val result = DiffUtil.calculateDiff(diffUtil)
        slots = newData
        result.dispatchUpdatesTo(this)
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 1
        private const val VIEW_TYPE_BOOKING = 2
    }

    interface OnClickListener {
        fun onDetailOrderClickedClicked(booking: BookingModel)
        fun onChatClicked(petOwner: UserModel, petShopModel: PetShopModel)
    }
}