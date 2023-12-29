package com.andriawan24.pawpalace.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.databinding.ViewHistoryItemBinding
import com.andriawan24.pawpalace.utils.RecyclerDiffUtil
import java.text.NumberFormat
import java.util.EventListener
import java.util.Locale
import java.util.concurrent.TimeUnit

class HistoryItemAdapter(private val listener: OnClickListener): RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {

    private var histories = emptyList<BookingModel>()

    class HistoryItemViewHolder(private val binding: ViewHistoryItemBinding, private val listener: OnClickListener): RecyclerView.ViewHolder(binding.root) {

        fun bind(history: BookingModel) {
            binding.root.setOnClickListener {
                listener.onHistoryItemClicked(history)
            }
            binding.buttonGiveRating.setOnClickListener {
                listener.onGiveRatingButtonClicked(history)
            }

            binding.textViewName.text = history.petShop.name
            binding.textViewAddress.text = history.petShop.location
            val days = TimeUnit.MILLISECONDS.toDays(history.endDate.time - history.startDate.time) + 1
            val totalPrice = history.petShop.dailyPrice * days
            val numberFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.textViewPrice.text = numberFormatter.format(totalPrice)
            val status = BookingModel.Status.valueOf(history.status.uppercase())
            binding.textViewStatus.text = status.statusName.titleCase()
            when(status) {
                BookingModel.Status.PENDING -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.yellow))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.yellow_background))
                    binding.buttonGiveRating.visibility = GONE
                    binding.linearLayoutRating.visibility = GONE
                }
                BookingModel.Status.PROCESS -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.yellow))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.yellow_background))
                    binding.buttonGiveRating.visibility = GONE
                    binding.linearLayoutRating.visibility = GONE
                }
                BookingModel.Status.DONE -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.green))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.green_background))
                    binding.buttonGiveRating.visibility = VISIBLE
                    binding.linearLayoutRating.visibility = GONE
                }
                BookingModel.Status.REVIEWED -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.green))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.green_background))
                    binding.buttonGiveRating.visibility = GONE
                    binding.linearLayoutRating.visibility = VISIBLE
                    binding.textViewRating.text = "${history.rating}"
                    binding.textViewStatus.text = BookingModel.Status.DONE.statusName.titleCase()
                }
            }
        }

        private fun String.titleCase(): String {
            return this.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        }

        private fun getColor(@ColorRes colorRes: Int): Int {
            return ContextCompat.getColor(binding.root.context, colorRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val binding = ViewHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bind(histories[position])
    }

    override fun getItemCount(): Int {
        return histories.size
    }

    fun setData(newItems: List<BookingModel>) {
        val diffUtil = RecyclerDiffUtil(newItems, histories)
        val result = DiffUtil.calculateDiff(diffUtil)
        histories = newItems
        result.dispatchUpdatesTo(this)
    }

    interface OnClickListener {
        fun onHistoryItemClicked(booking: BookingModel)
        fun onGiveRatingButtonClicked(booking: BookingModel)
    }
}