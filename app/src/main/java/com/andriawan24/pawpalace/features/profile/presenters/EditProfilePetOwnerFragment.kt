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
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.databinding.FragmentEditProfilePetOwnerBinding
import com.andriawan24.pawpalace.features.profile.viewmodels.EditProfileVM
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfilePetOwnerFragment: BaseFragment<FragmentEditProfilePetOwnerBinding, EditProfileVM>() {

    private val args: EditProfilePetOwnerFragmentArgs by navArgs()
    private val imageChooser = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if (it != null) {
            handleImage(it)
        }
    }
    override val viewModel: EditProfileVM by viewModels()
    override val binding: FragmentEditProfilePetOwnerBinding by lazy {
        FragmentEditProfilePetOwnerBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initUser(args.user, Firebase.auth.currentUser?.photoUrl)
        binding.buttonEditImage.setOnClickListener {
            imageChooser.launch(PickVisualMediaRequest())
        }
        binding.buttonSaveChanges.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val phoneNumber = binding.editTextPhone.text.toString()
            viewModel.updateProfile(name, phoneNumber, args.user)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initUser(user: UserModel, imageUri: Uri?) {
        binding.editTextName.setText(user.name)
        binding.editTextEmail.setText(user.email)
        binding.editTextPhone.setText(user.phoneNumber)
        if (imageUri != null) {
            binding.imageViewProfile.setImageURI(imageUri)
        }
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