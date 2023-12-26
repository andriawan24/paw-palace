package com.andriawan24.pawpalace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.databinding.ViewPetShopItemBinding
import com.andriawan24.pawpalace.utils.RecyclerDiffUtil

class PetShopItemAdapter: RecyclerView.Adapter<PetShopItemAdapter.PetShopItemViewHolder>() {

    private var petShops = emptyList<PetShopModel>()

    class PetShopItemViewHolder(
        private val binding: ViewPetShopItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(petShop: PetShopModel) {
            binding.textViewName.text = petShop.name
            binding.textViewRating.text = "${petShop.rating}"
            binding.textViewSlotPrice.text = "${petShop.slot} Slot • ${petShop.dailyPrice}/hari"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetShopItemViewHolder {
        val binding = ViewPetShopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PetShopItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetShopItemViewHolder, position: Int) {
        holder.bind(petShops[position])
    }

    override fun getItemCount(): Int {
        return petShops.size
    }

    fun setData(newItems: List<PetShopModel>) {
        val diffUtil = RecyclerDiffUtil(petShops, newItems)
        val result = DiffUtil.calculateDiff(diffUtil)
        petShops = newItems
        result.dispatchUpdatesTo(this)
    }
}