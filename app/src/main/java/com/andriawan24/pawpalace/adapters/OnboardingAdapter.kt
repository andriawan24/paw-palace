package com.andriawan24.pawpalace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.data.models.OnboardingModel
import com.andriawan24.pawpalace.databinding.ViewOnboardingItemBinding
import com.andriawan24.pawpalace.utils.RecyclerDiffUtil

class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnboardingItemViewHolder>() {

    private var onboardingItems: List<OnboardingModel> = emptyList()

    class OnboardingItemViewHolder(
        private val binding: ViewOnboardingItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onboardingItem: OnboardingModel) {
            binding.imageViewOnboarding.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    onboardingItem.image
                )
            )
            binding.textViewTitle.text = onboardingItem.title
            binding.textViewSubtitle.text = onboardingItem.subtitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
        val binding =
            ViewOnboardingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(onboardingItem = onboardingItems[position])
    }

    override fun getItemCount(): Int = onboardingItems.size

    fun setData(newData: List<OnboardingModel>) {
        val diffUtil = RecyclerDiffUtil(onboardingItems, newData)
        val result = DiffUtil.calculateDiff(diffUtil)
        onboardingItems = newData
        result.dispatchUpdatesTo(this)
    }
}