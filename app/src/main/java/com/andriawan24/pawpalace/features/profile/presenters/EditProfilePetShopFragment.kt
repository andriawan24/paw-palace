package com.andriawan24.pawpalace.features.profile.presenters

import android.net.Uri
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.databinding.FragmentEditProfilePetShopBinding
import com.andriawan24.pawpalace.features.profile.viewmodels.EditProfileVM
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfilePetShopFragment :
    BaseFragment<FragmentEditProfilePetShopBinding, EditProfileVM>() {

    private val args: EditProfilePetShopFragmentArgs by navArgs()
    private val imageChooser =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                handleImage(it)
            }
        }
    override val viewModel: EditProfileVM by viewModels()
    override val binding: FragmentEditProfilePetShopBinding by lazy {
        FragmentEditProfilePetShopBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initUser(args.user, Firebase.auth.currentUser?.photoUrl, args.petShop)
        binding.buttonEditImage.setOnClickListener {
            imageChooser.launch(PickVisualMediaRequest())
        }
        binding.buttonSaveChanges.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val phoneNumber = binding.editTextPhone.text.toString()
            val description = binding.editTextDescription.text.toString()
            val location = binding.editTextLocation.text.toString()
            val dailyPrice = binding.editTextDailyPrice.text.toString().toIntOrNull() ?: 0
            val slot = binding.editTextSlot.text.toString().toIntOrNull() ?: 0
            viewModel.updatePetShop(
                name = name,
                phoneNumber = phoneNumber,
                description = description,
                location = location,
                dailyPrice = dailyPrice,
                slot = slot,
                user = args.user
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initUser(user: UserModel, imageUri: Uri?, petShop: PetShopModel) {
        binding.editTextName.setText(petShop.name)
        binding.editTextEmail.setText(user.email)
        binding.editTextPhone.setText(user.phoneNumber)
        if (imageUri != null) {
            binding.imageViewProfile.setImageURI(imageUri)
        }
        binding.editTextDescription.setText(petShop.description)
        binding.editTextSlot.setText(petShop.slot.toString())
        binding.editTextLocation.setText(petShop.location)
        binding.editTextDailyPrice.setText(petShop.dailyPrice.toString())
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.isEditProfileSuccess.collectLatest {
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            viewModel.isEditProfileError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleImage(imageUri: Uri) {
        binding.imageViewProfile.setImageURI(imageUri)
        viewModel.updateImageUri(imageUri)
    }
}