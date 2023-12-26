package com.andriawan24.pawpalace.adapters

import androidx.recyclerview.widget.RecyclerView
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.databinding.ViewPetShopItemBinding

class PetShopItemAdapter: RecyclerView.Adapter<PetShopItemAdapter.PetShopItemViewHolder>() {

    private var petShops = emptyArray<PetShopModel>()

    class PetShopItemViewHolder(private val binding: ViewPetShopItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun getItemCount(): Int {
        return petShops.size
    }
}