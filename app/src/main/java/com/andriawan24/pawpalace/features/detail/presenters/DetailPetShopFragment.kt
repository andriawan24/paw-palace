package com.andriawan24.pawpalace.features.detail.presenters

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.databinding.FragmentDetailPetShopBinding
import com.andriawan24.pawpalace.features.detail.viewmodels.DetailPetShopVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPetShopFragment : BaseFragment<FragmentDetailPetShopBinding, DetailPetShopVM>() {

    private val args: DetailPetShopFragmentArgs by navArgs()
    override val viewModel: DetailPetShopVM by viewModels()
    override val binding: FragmentDetailPetShopBinding by lazy {
        FragmentDetailPetShopBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initClickListener()
        viewModel.initData(args.petShopId)
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.getDetailSuccess.collectLatest {
                binding.textViewSlot.text = "${it.slot} Slot(s) Available"
                binding.textViewName.text = it.name
                binding.textViewPrice.text = it.dailyPrice.toString()
                binding.textViewAddress.text = it.location.ifEmpty { "Location not set" }
                binding.textViewRating.text = it.rating.toString()
                binding.textViewDescription.text = it.description
            }
        }

        lifecycleScope.launch {
            viewModel.getDetailError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.goToMessageRoom.collectLatest {
                findNavController().navigate(
                    DetailPetShopFragmentDirections.actionDetailPetShopFragmentToChatDetailFragment(
                        petShop = it
                    )
                )
            }
        }
    }

    private fun initClickListener() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonChat.setOnClickListener {
            viewModel.onMessageClicked()
        }

        binding.buttonBooking.setOnClickListener {
            viewModel.petShopState.value?.let {
                findNavController().navigate(
                    DetailPetShopFragmentDirections
                        .actionDetailPetShopFragmentToBookingFormFragment(it)
                )
            }
        }
    }
}