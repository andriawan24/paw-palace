package com.andriawan24.pawpalace.features.register.presenters

import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.databinding.FragmentCompleteRegisterPetShopBinding
import com.andriawan24.pawpalace.features.register.viewmodels.CompleteRegisterPetShopVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompleteRegisterPetShopFragment: BaseFragment<FragmentCompleteRegisterPetShopBinding, CompleteRegisterPetShopVM>() {

    private val args: CompleteRegisterPetShopFragmentArgs by navArgs()
    override val viewModel: CompleteRegisterPetShopVM by viewModels()
    override val binding: FragmentCompleteRegisterPetShopBinding by lazy {
        FragmentCompleteRegisterPetShopBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initClickListener()
        initTextListener()
    }

    private fun initTextListener() {
        binding.editTextDescription.doAfterTextChanged { validateDescription(it.toString()) }
        binding.editTextLocation.doAfterTextChanged { validateLocation(it.toString()) }
        binding.editTextDailyPrice.doAfterTextChanged { validateDailyPrice(it.toString()) }
        binding.editTextSlot.doAfterTextChanged { validateSlot(it.toString()) }
    }

    private fun initClickListener() {
        binding.buttonSave.setOnClickListener {
            val description = binding.editTextDescription.text.toString()
            val location = binding.editTextLocation.text.toString()
            val dailyPrice = binding.editTextDailyPrice.text.toString()
            val slot = binding.editTextSlot.text.toString()

            if (validateInput(description, location, dailyPrice, slot)) {
                viewModel.completeRegister(
                    args.petShopId,
                    description,
                    location,
                    dailyPrice.toInt(),
                    slot.toInt()
                )
            }
        }
    }

    private fun validateInput(
        description: String,
        location: String,
        dailyPrice: String,
        slot: String
    ): Boolean {
        var isValid = true

        if (!validateDescription(description)) {
            isValid = false
        }

        if (!validateLocation(location)) {
            isValid = false
        }

        if (!validateDailyPrice(dailyPrice)) {
            isValid = false
        }

        if (!validateSlot(slot)) {
            isValid = false
        }

        return isValid
    }

    private fun validateDescription(description: String): Boolean {
        if (description.isBlank()) {
            binding.editTextLayoutDescription.isErrorEnabled = true
            binding.editTextLayoutDescription.error = "Description cannot be empty"
            return false
        }

        binding.editTextLayoutDescription.error = null
        return true
    }

    private fun validateLocation(location: String): Boolean {
        if (location.isBlank()) {
            binding.editTextLayoutLocation.isErrorEnabled = true
            binding.editTextLayoutLocation.error = "Location cannot be empty"
            return false
        }

        binding.editTextLayoutLocation.error = null
        return true
    }

    private fun validateDailyPrice(dailyPrice: String): Boolean {
        if (dailyPrice.isBlank()) {
            binding.editTextLayoutDailyPrice.isErrorEnabled = true
            binding.editTextLayoutDailyPrice.error = "Daily price cannot be empty"
            return false
        } else if (!dailyPrice.isDigitsOnly()) {
            binding.editTextLayoutDailyPrice.isErrorEnabled = true
            binding.editTextLayoutDailyPrice.error = "Daily price must be digit only"
            return false
        } else if (dailyPrice.toInt() < 0) {
            binding.editTextLayoutDailyPrice.isErrorEnabled = true
            binding.editTextLayoutDailyPrice.error = "Daily price must be greater than 0"
            return false
        }

        binding.editTextLayoutDailyPrice.error = null
        return true
    }

    private fun validateSlot(slot: String): Boolean {
        if (slot.isBlank()) {
            binding.editTextLayoutSlot.isErrorEnabled = true
            binding.editTextLayoutSlot.error = "Slot cannot be empty"
            return false
        } else if (!slot.isDigitsOnly()) {
            binding.editTextLayoutSlot.isErrorEnabled = true
            binding.editTextLayoutSlot.error = "Slot must be digit only"
            return false
        } else if (slot.toInt() < 0) {
            binding.editTextLayoutSlot.isErrorEnabled = true
            binding.editTextLayoutSlot.error = "Slot must be greater than 0"
            return false
        }

        binding.editTextLayoutSlot.error = null
        return true
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.isCompleteRegisterLoading.collectLatest {
                binding.buttonSave.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            viewModel.completeRegisterSuccess.collectLatest {
                findNavController().navigate(R.id.action_global_home)
            }
        }

        lifecycleScope.launch {
            viewModel.completeRegisterError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}